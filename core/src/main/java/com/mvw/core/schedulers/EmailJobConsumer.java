package com.mvw.core.schedulers;

import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.mvw.core.services.impl.MvwGoverningRegistrationBoxes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jcr.Session;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

@Component(service = JobConsumer.class, property = {
        JobConsumer.PROPERTY_TOPICS + "=com/mvw/jobs/email/send"
})
public class EmailJobConsumer implements JobConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(EmailJobConsumer.class);

    // Service user must have jcr:read on /var/workflow (and traversal)
    private static final String SUBSERVICE = "mvwServiceReader"; // or mvwServiceReader if read-only
    private static final String BULK_PUBLISH_NOTIFICATION_TEMPLATE = "/etc/notification/email/legal/bulk-publish-notification.txt";
    private static final String WORKFLOW_ROOT = "/var/workflow/instances";
    private static final String MARRIOTT_DAM_ROOT = "/content/dam/legal/marriottvacationclubs";
    private static final String HYATT_DAM_ROOT = "/content/dam/legal/hyattvacationclub";

    private static final long HOUR_MS = 60L * 60L * 1000L;
    private static final long MIN_MS_48H = 48L * HOUR_MS;
    private static final long MAX_MS_72H = 72L * HOUR_MS;

    private static final ZoneId NY = ZoneId.of("America/New_York");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private QueryBuilder queryBuilder;

    @Reference
    private MessageGatewayService messageGatewayService;

    @Reference
    private SlingSettingsService slingSettingsService;

    @Reference
    private MvwGoverningRegistrationBoxes registrationBoxes;

    @Override
    public JobResult process(Job job) {
        LOG.info("EmailJobConsumer started.");
        long now = System.currentTimeMillis();
        LOG.info("current time ms={}", now);

        try (ResourceResolver resourceResolver = getServiceResolver()) {
            Session session = resourceResolver.adaptTo(Session.class);

            if (session == null) {
                LOG.info("No JCR Session from service resolver.");
                return JobResult.FAILED;
            }
            LOG.info("session permission:" + session.getUserID());
            LOG.info("session permission FOR READ:"
                    + session.hasPermission(WORKFLOW_ROOT, Session.ACTION_READ));
            LOG.info("session permission on instance for read:{} path: {}"
                    + session.hasPermission(WORKFLOW_ROOT + "/instances", Session.ACTION_READ),
                    WORKFLOW_ROOT + "/instances");
            LOG.info("workflow root path:" + WORKFLOW_ROOT);

            // Query workflow instances that are pending AND have absoluteTime present.
            // Note: workflow instance nodes are typically cq:Workflow under
            // /var/workflow/instances.
            Map<String, String> predicates = new HashMap<>();
            predicates.put("path", WORKFLOW_ROOT);
            predicates.put("type", "cq:Workflow");

            predicates.put("property", "modelId");
            predicates.put("property.value", "/var/workflow/models/scheduled_tree_activation");

            /*
             * Map.entry("property.2_name", "data/payload/path"),
             * Map.entry("property.2_operation", "like"),
             * Map.entry("property.2_value", "/content/dam/legal/%"),
             * 
             * Map.entry("property.3_name", "data/metaData/absoluteTime"),
             * Map.entry("property.3_operation", "exists"),
             */

            predicates.put("p.limit", "-1");

            Query query = queryBuilder.createQuery(PredicateGroup.create(predicates), session);
            SearchResult result = query.getResult();

            int matched = 0;
            int logged = 0;
            List<WorkflowMetadata> marriottWorkflows = new ArrayList<>();
            List<WorkflowMetadata> hyattWorkflows = new ArrayList<>();
            LOG.info("size:" + result.getHits().size());

            for (Hit hit : result.getHits()) {
                matched++;

                String wfPath;
                try {
                    wfPath = hit.getPath();
                    LOG.info("wfPath={}", wfPath);
                } catch (Exception e) {
                    LOG.info("Skipping hit (cannot read path)", e);
                    continue;
                }

                Resource wf = resourceResolver.getResource(wfPath);
                LOG.info("wf={}", wf);
                if (wf == null)
                    continue;

                String payload = readPayload(resourceResolver, wfPath);
                if (StringUtils.isBlank(payload))
                    continue;

                WorkflowMetadata workflowMetadata = getMetaData(resourceResolver, wfPath);
                if (workflowMetadata == null)
                    continue;
                workflowMetadata = workflowMetadata.withPaths(wfPath, payload);
                LOG.info("workflow metadata={} ", workflowMetadata);

                long diff = workflowMetadata.getEpochMillis() - now; // time remaining until scheduled moment

                // user requirement: above 48h and below 72h
                LOG.info("current workflow diff check: wfPath={} epochMillis={} diff={}", wfPath,
                        workflowMetadata.getEpochMillis(), diff);
                if (diff > MIN_MS_48H && diff < MAX_MS_72H) {
                    String status = wf.getValueMap().get("status", "");
                    if (status.equalsIgnoreCase("running")) {

                        String humanNY = FMT.format(Instant.ofEpochMilli(workflowMetadata.getEpochMillis()).atZone(NY));
                        long diffHours = diff / HOUR_MS;

                        LOG.info(
                                "PENDING WORKFLOW in 48-72h window | status={} | diffHours~{} | absoluteTimeMs={} | absoluteTimeNY={} | workflow={} | payload={}",
                                status, diffHours, workflowMetadata.getEpochMillis(), humanNY, wfPath, payload);
                        if (payload.contains(MARRIOTT_DAM_ROOT)) {
                            marriottWorkflows.add(workflowMetadata);
                        } else if (payload.contains(HYATT_DAM_ROOT)) {
                            hyattWorkflows.add(workflowMetadata);
                        }
                        logged++;
                    }
                }
            }

            LOG.info("Marriott workflow list={}", marriottWorkflows);
            LOG.info("Hyatt workflow list={}", hyattWorkflows);
            sendBulkPublishEmail(resourceResolver, marriottWorkflows,
                    registrationBoxes != null ? registrationBoxes.getMarriottsRegistrationBox() : null, "Marriott");
            sendBulkPublishEmail(resourceResolver, hyattWorkflows,
                    registrationBoxes != null ? registrationBoxes.getHyattRegistrationBox() : null, "Hyatt");
            LOG.info("EmailJobConsumer completed. Checked={} Logged={}", matched, logged);
            return JobResult.OK;

        } catch (Exception e) {
            LOG.info("EmailJobConsumer failed", e);
            return JobResult.FAILED;
        }
    }

    private ResourceResolver getServiceResolver() throws LoginException {
        return resolverFactory.getServiceResourceResolver(
                Map.of(ResourceResolverFactory.SUBSERVICE, SUBSERVICE));
    }

    private void sendBulkPublishEmail(ResourceResolver resolver, List<WorkflowMetadata> workflows, String recipients,
            String brand) {
        if (workflows.isEmpty()) {
            LOG.info("No {} workflows in 48-72h window. Skipping email.", brand);
            return;
        }
        if (StringUtils.isBlank(recipients)) {
            LOG.warn("{} registration box is blank. Skipping bulk publish email.", brand);
            return;
        }
        if (registrationBoxes == null || StringUtils.isBlank(registrationBoxes.getFromAddress())) {
            LOG.warn("From address is blank. Skipping {} bulk publish email.", brand);
            return;
        }

        MessageGateway<HtmlEmail> gateway = messageGatewayService.getGateway(HtmlEmail.class);
        if (gateway == null) {
            LOG.warn("Email gateway unavailable. Skipping {} bulk publish email.", brand);
            return;
        }

        Session session = resolver.adaptTo(Session.class);
        if (session == null) {
            LOG.warn("No JCR session available for mail template. Skipping {} bulk publish email.", brand);
            return;
        }

        try {
            MailTemplate mailTemplate = MailTemplate.create(BULK_PUBLISH_NOTIFICATION_TEMPLATE, session);
            if (mailTemplate == null) {
                LOG.warn("Template not found: {}", BULK_PUBLISH_NOTIFICATION_TEMPLATE);
                return;
            }

            Map<String, String> emailParams = new HashMap<>();
            emailParams.put("environmentName", getEnvironmentName());
            emailParams.put("documentRows", buildDocumentRows(workflows));

            HtmlEmail email = mailTemplate.getEmail(emailParams, HtmlEmail.class);
            if (email == null) {
                LOG.warn("Mail template returned null email for {} bulk publish notification.", brand);
                return;
            }
            email.setCharset("UTF-8");
            email.setFrom(registrationBoxes.getFromAddress());
            addRecipients(email, recipients);

            gateway.send(email);
            LOG.info("{} bulk publish email sent to {}", brand, recipients);
        } catch (Exception e) {
            LOG.error("Failed to send {} bulk publish email", brand, e);
        }
    }

    private void addRecipients(HtmlEmail email, String recipients) throws Exception {
        for (String recipient : recipients.split("[,;]")) {
            String trimmedRecipient = StringUtils.trimToNull(recipient);
            if (trimmedRecipient != null) {
                email.addTo(trimmedRecipient);
            }
        }
    }

    private String buildDocumentRows(List<WorkflowMetadata> workflows) {
        StringBuilder rows = new StringBuilder();
        for (Map<String, String> workflowMap : buildWorkflowTemplateMaps(workflows)) {
            rows.append("<tr>")
                    .append(buildCell(workflowMap.get("title")))
                    .append(buildCell(workflowMap.get("scheduledPublishDate")))
                    .append(buildCell(workflowMap.get("timeSharePlan")))
                    .append(buildCell(workflowMap.get("userId")))
                    .append("</tr>");
        }
        return rows.toString();
    }

    private List<Map<String, String>> buildWorkflowTemplateMaps(List<WorkflowMetadata> workflows) {
        List<Map<String, String>> workflowMaps = new ArrayList<>();
        for (WorkflowMetadata workflow : workflows) {
            workflowMaps.add(workflow.toTemplateMap());
        }
        return workflowMaps;
    }

    private String buildCell(String value) {
        return "<td>" + escapeHtml(StringUtils.defaultString(value)) + "</td>";
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String getEnvironmentName() {
        if (slingSettingsService == null) {
            return "AEM";
        }

        for (String runMode : slingSettingsService.getRunModes()) {
            if ("prod".equals(runMode) || "production".equals(runMode)) {
                return "Production";
            }
            if ("stage".equals(runMode) || "staging".equals(runMode)) {
                return "Stage";
            }
            if ("qa".equals(runMode)) {
                return "QA";
            }
            if ("uat".equals(runMode)) {
                return "UAT";
            }
            if ("dev".equals(runMode) || "development".equals(runMode)) {
                return "Development";
            }
            if ("local".equals(runMode)) {
                return "Local";
            }
        }
        return "AEM";
    }

    /**
     * Reads required metadata from /var/workflow/instances/.../<id>/data/metaData
     */
    private WorkflowMetadata getMetaData(ResourceResolver rr, String wfPath) {
        Resource meta = rr.getResource(wfPath + "/data/metaData");
        if (meta == null)
            return null;

        String title = StringUtils.trimToNull(meta.getValueMap().get("title", String.class));
        String userId = StringUtils.trimToNull(meta.getValueMap().get("userId", String.class));
        String scheduledPublishDate = StringUtils
                .trimToNull(meta.getValueMap().get("scheduled_publish_date", String.class));
        Long epochMillis = toEpochMillis(meta.getValueMap().get("absoluteTime"));
        String timeSharePlan = StringUtils.trimToNull(meta.getValueMap().get("timeSharePlan", String.class));

        if (userId == null || scheduledPublishDate == null || epochMillis == null)
            return null;

        return new WorkflowMetadata(null, null, title, userId, scheduledPublishDate, epochMillis, timeSharePlan);
    }

    /**
     * Reads /var/workflow/instances/.../<id>/data/payload (typical location)
     */
    private String readPayload(ResourceResolver rr, String wfPath) {
        Resource data = rr.getResource(wfPath + "/data/payload");
        if (data == null)
            return null;

        String payload = data.getValueMap().get("path", String.class);
        return StringUtils.defaultIfBlank(payload, null);
    }

    private Long toEpochMillis(Object v) {
        if (v == null)
            return null;

        if (v instanceof Long)
            return (Long) v;
        if (v instanceof Integer)
            return ((Integer) v).longValue();

        if (v instanceof String) {
            String s = ((String) v).trim();
            if (s.matches("\\d+"))
                return Long.parseLong(s);
            return null;
        }

        if (v instanceof Calendar)
            return ((Calendar) v).getTimeInMillis();
        if (v instanceof java.util.Date)
            return ((java.util.Date) v).getTime();

        return null;
    }

    private static final class WorkflowMetadata {
        private final String workflowPath;
        private final String payloadPath;
        private final String title;
        private final String userId;
        private final String scheduledPublishDate;
        private final Long epochMillis;
        private final String timeSharePlan;

        private WorkflowMetadata(String workflowPath, String payloadPath, String title, String userId,
                String scheduledPublishDate, Long epochMillis, String timeSharePlan) {
            this.workflowPath = workflowPath;
            this.payloadPath = payloadPath;
            this.title = title;
            this.userId = userId;
            this.scheduledPublishDate = scheduledPublishDate;
            this.epochMillis = epochMillis;
            this.timeSharePlan = timeSharePlan;
        }

        private WorkflowMetadata withPaths(String workflowPath, String payloadPath) {
            return new WorkflowMetadata(workflowPath, payloadPath, title, userId, scheduledPublishDate, epochMillis,
                    timeSharePlan);
        }

        private Map<String, String> toTemplateMap() {
            Map<String, String> templateMap = new HashMap<>();
            templateMap.put("title", StringUtils.defaultString(title));
            templateMap.put("userId", StringUtils.defaultString(userId));
            templateMap.put("scheduledPublishDate", StringUtils.defaultString(scheduledPublishDate));
            templateMap.put("timeSharePlan", StringUtils.defaultString(timeSharePlan));
            return Collections.unmodifiableMap(templateMap);
        }

        private Long getEpochMillis() {
            return epochMillis;
        }

        @Override
        public String toString() {
            return "WorkflowMetadata{" +
                    "workflowPath='" + workflowPath + '\'' +
                    ", payloadPath='" + payloadPath + '\'' +
                    ", title='" + title + '\'' +
                    ", userId='" + userId + '\'' +
                    ", scheduledPublishDate='" + scheduledPublishDate + '\'' +
                    ", epochMillis=" + epochMillis +
                    ", timeSharePlan='" + timeSharePlan + '\'' +
                    '}';
        }
    }
}
