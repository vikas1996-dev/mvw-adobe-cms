package com.mvw.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.mvw.core.utils.WorkflowUtils;
import org.apache.commons.mail.HtmlEmail;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.*;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.*;
import java.util.stream.Collectors;

@Component(service = WorkflowProcess.class, property = {"process.label=Send Targeted Email Notification Process"})
public class EmailNotificationProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationProcess.class);
    private static final String SUB_SERVICE = "mvwWorkflowServiceUser";
    private static final String FROM_EMAIL = "donotreply@mvwc.com";
    private static final String FROM_NAME = "MVWC Workflow";

    @Reference
    private MessageGatewayService messageGatewayService;

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private Externalizer externalizer;

    @Reference
    private SlingSettingsService slingSettingsService;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) throws WorkflowException {
        String payloadPath = workItem.getWorkflowData().getPayload().toString();

        Map<String, String> processArguments = WorkflowUtils.parseArgumentsToMap(args.get("PROCESS_ARGS", ""));
        String templatePath = processArguments.getOrDefault("templatePath", "/etc/notification/email/tmvc/workflow-notification.txt");
        String requestedReviewers = processArguments.getOrDefault("reviewers", "");
        String notificationType = processArguments.getOrDefault("notificationType", "");

        MetaDataMap workflowMetaData = workItem.getWorkflow().getMetaDataMap();
        MetaDataMap workflowDataMetaData = workItem.getWorkflowData().getMetaDataMap();
        Set<String> authIds = new HashSet<>();

        if (!requestedReviewers.isEmpty()) {
            String[] reviewerKeys = requestedReviewers.split(",");
            for (String key : reviewerKeys) {
                String propValue = workflowMetaData.get(key.trim(), String.class);
                if (propValue == null || propValue.isEmpty()) {
                    propValue = workflowDataMetaData.get(key.trim(), String.class);
                }
                // Fallback: "initiator" is often on Workflow object, not in metadata
                if ((propValue == null || propValue.isEmpty()) && "initiator".equalsIgnoreCase(key.trim())) {
                    String initiator = workItem.getWorkflow().getInitiator();
                    if (initiator != null && !initiator.isEmpty()) {
                        propValue = initiator;
                        log.debug("Using workflow initiator from Workflow.getInitiator(): {}", initiator);
                    }
                }
                
                if (propValue != null && !propValue.isEmpty()) {
                    authIds.add(propValue);
                    log.debug("Found reviewer '{}' with value '{}' from workflow metadata", key.trim(), propValue);
                }
            }
        }

        if (authIds.isEmpty()) {
            log.warn("No reviewer IDs found in workflow metadata for reviewers argument: {}", requestedReviewers);
            return;
        }

        String comments = workflowMetaData.get("comments", String.class);
        if (comments == null || comments.isEmpty()) {
            comments = workflowDataMetaData.get("comment", String.class);
        }
        if (comments == null) {
            comments = "";
        }

        // 5. Get environment name dynamically from run modes
        String environmentName = getEnvironmentName();

        Map<String, Object> authInfo = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SUB_SERVICE);

        try (ResourceResolver serviceResolver = resolverFactory.getServiceResourceResolver(authInfo)) {
            // 6. Resolve IDs (Users or Groups) to User Info (email and name)
            Map<String, String> userInfoMap = resolveUserInfo(authIds, serviceResolver);

            if (!userInfoMap.isEmpty()) {
                sendTemplatedEmail(userInfoMap, templatePath, payloadPath, workItem, serviceResolver, environmentName, comments, notificationType);
            } else {
                log.warn("No email recipients resolved for reviewers: {}", authIds);
            }

        } catch (LoginException e) {
            log.error("Service User login failed for {}", SUB_SERVICE, e);
            throw new WorkflowException("Auth error", e);
        }
    }

    /**
     * Resolves user/group IDs to a map of email addresses to display names.
     * @param authIds Set of user or group IDs
     * @param resolver ResourceResolver for accessing user manager
     * @return Map of email address to user display name
     */
    private Map<String, String> resolveUserInfo(Set<String> authIds, ResourceResolver resolver) {
        Map<String, String> userInfoMap = new HashMap<>();
        UserManager userManager = resolver.adaptTo(UserManager.class);
        if (userManager == null) return userInfoMap;
        try {
            for (String id : authIds) {
                Authorizable auth = userManager.getAuthorizable(id);
                if (auth == null) continue;
                if (auth.isGroup()) {
                    Iterator<Authorizable> members = ((Group) auth).getMembers();
                    while (members.hasNext()) {
                        extractUserInfo(members.next(), userInfoMap);
                    }
                } else {
                    extractUserInfo(auth, userInfoMap);
                }
            }
        } catch (Exception e) {
            log.error("Error resolving user info", e);
        }
        return userInfoMap;
    }

    /**
     * Extracts email and display name from an Authorizable and adds to the map.
     * @param auth The Authorizable (user) to extract info from
     * @param userInfoMap Map to add the user info to (email -> displayName)
     */
    private void extractUserInfo(Authorizable auth, Map<String, String> userInfoMap) throws Exception {
        if (auth != null && !auth.isGroup()) {
            String email = null;
            String displayName = null;

            // Get email
            javax.jcr.Value[] emailValues = auth.getProperty("./profile/email");
            if (emailValues != null && emailValues.length > 0) {
                email = emailValues[0].getString();
            }

            // Get display name (givenName + familyName or fallback to ID)
            javax.jcr.Value[] givenNameValues = auth.getProperty("./profile/givenName");
            javax.jcr.Value[] familyNameValues = auth.getProperty("./profile/familyName");
            
            StringBuilder nameBuilder = new StringBuilder();
            if (givenNameValues != null && givenNameValues.length > 0) {
                nameBuilder.append(givenNameValues[0].getString());
            }
            if (familyNameValues != null && familyNameValues.length > 0) {
                if (nameBuilder.length() > 0) {
                    nameBuilder.append(" ");
                }
                nameBuilder.append(familyNameValues[0].getString());
            }
            
            displayName = nameBuilder.length() > 0 ? nameBuilder.toString() : auth.getID();

            if (email != null && email.contains("@")) {
                userInfoMap.put(email, displayName);
            }
        }
    }

    /**
     * Sends personalized templated emails to each recipient.
     * Template variables: ${userName}, ${environmentName}, ${previewLinks},
     * ${approvalLink}, ${commentsSection}, ${payload}.
     */
    private void sendTemplatedEmail(Map<String, String> userInfoMap, String templatePath, String payload, 
                                    WorkItem workItem, ResourceResolver resolver, 
                                    String environmentName, String comments, String notificationType) {
        try {
            MessageGateway<HtmlEmail> gateway = messageGatewayService.getGateway(HtmlEmail.class);
            if (gateway == null) {
                log.error("Email gateway not available");
                return;
            }

            MailTemplate mailTemplate = MailTemplate.create(templatePath, resolver.adaptTo(Session.class));
            if (mailTemplate == null) {
                log.error("Template not found: {}", templatePath);
                return;
            }

            // Build preview links
            String previewLinks = buildPreviewLinks(payload, resolver);
            
            String baseInboxUrl = externalizer.authorLink(resolver, "/aem/inbox");
            String approvalLink = "feedback".equalsIgnoreCase(notificationType)
                    ? baseInboxUrl
                    : baseInboxUrl + "?item=" + workItem.getId();

            // Send personalized email to each recipient
            for (Map.Entry<String, String> entry : userInfoMap.entrySet()) {
                String recipientEmail = entry.getKey();
                String userName = entry.getValue();

                try {
                    Map<String, String> emailParams = new HashMap<>();
                    emailParams.put("userName", userName);
                    emailParams.put("environmentName", environmentName);
                    emailParams.put("previewLinks", previewLinks);
                    emailParams.put("approvalLink", approvalLink);
                    emailParams.put("commentsSection", buildCommentsSection(comments));
                    emailParams.put("payload", payload);

                    HtmlEmail email = mailTemplate.getEmail(emailParams, HtmlEmail.class);
                    email.setCharset("UTF-8");
                    email.setFrom(FROM_EMAIL, FROM_NAME);
                    email.addTo(recipientEmail, userName);

                    gateway.send(email);
                    log.info("Email sent to: {} ({})", userName, recipientEmail);
                } catch (Exception e) {
                    log.error("Failed to send email to {}: {}", recipientEmail, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to send email via gateway", e);
        }
    }

    /**
     * Builds preview links for the payload content.
     * @param payload The workflow payload path
     * @param resolver ResourceResolver for building links
     * @return Formatted preview links string
     */
    private String buildPreviewLinks(String payload, ResourceResolver resolver) {
        return externalizer.authorLink(resolver, payload) + ".html";
    }

    /**
     * Builds the comments section for the email template.
     * Returns a formatted comments block if comments exist, empty string otherwise.
     * @param comments The workflow comments
     * @return Formatted comments section or empty string
     */
    private String buildCommentsSection(String comments) {
        if (comments == null || comments.trim().isEmpty()) {
            return "";
        }
        return "\nComments: " + comments + "\n";
    }

    /**
     * Dynamically determines the environment name from AEM run modes.
     * Checks for common environment run modes like dev, stage, qa, prod, etc.
     * @return The environment name based on current run modes
     */
    private String getEnvironmentName() {
        Set<String> runModes = slingSettingsService.getRunModes();
        
        // Check for specific environment run modes (order matters - more specific first)
        if (runModes.contains("prod") || runModes.contains("production")) {
            return "Production";
        } else if (runModes.contains("stage") || runModes.contains("staging")) {
            return "Stage";
        } else if (runModes.contains("qa")) {
            return "QA";
        } else if (runModes.contains("uat")) {
            return "UAT";
        } else if (runModes.contains("dev") || runModes.contains("development")) {
            return "Development";
        } else if (runModes.contains("local")) {
            return "Local";
        }
        
        // Check for author/publish mode as fallback
        if (runModes.contains("author")) {
            return "Author";
        } else if (runModes.contains("publish")) {
            return "Publish";
        }
        
        // Default fallback
        return "AEM";
    }
}
