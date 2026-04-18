package com.mvw.core.workflow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.commons.mail.HtmlEmail;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmailNotificationProcessTest {

    @InjectMocks
    private EmailNotificationProcess process;

    @Mock
    private MessageGatewayService messageGatewayService;

    @Mock
    private ResourceResolverFactory resolverFactory;

    @Mock
    private Externalizer externalizer;

    @Mock
    private SlingSettingsService slingSettingsService;

    @Mock
    private WorkItem workItem;

    @Mock
    private WorkflowSession workflowSession;

    @Mock
    private MetaDataMap processArgs;

    @Mock
    private WorkflowData workflowData;

    @Mock
    private MetaDataMap workflowMetaData;

    @Mock
    private MetaDataMap workflowDataMetaData;

    @Mock
    private Workflow workflow;

    @Mock
    private ResourceResolver serviceResolver;

    @Mock
    private Session jcrSession;

    @Mock
    private UserManager userManager;

    @Mock
    private Authorizable userAuthorizable;

    @Mock
    private MessageGateway<HtmlEmail> messageGateway;

    @Mock
    private MailTemplate mailTemplate;

    private MockedStatic<MailTemplate> mailTemplateStatic;

    @BeforeEach
    void setUp() throws Exception {
        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn("/content/test/page");
        when(workflowData.getMetaDataMap()).thenReturn(workflowDataMetaData);
        when(workItem.getWorkflow()).thenReturn(workflow);
        when(workflow.getMetaDataMap()).thenReturn(workflowMetaData);
        when(workItem.getId()).thenReturn("work-item-123");

        Set<String> runModes = new HashSet<>();
        runModes.add("author");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);

        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(Session.class)).thenReturn(jcrSession);
        when(serviceResolver.adaptTo(UserManager.class)).thenReturn(userManager);

        when(externalizer.authorLink(eq(serviceResolver), eq("/aem/inbox"))).thenReturn("http://author/aem/inbox");
        when(externalizer.authorLink(eq(serviceResolver), eq("/content/test/page"))).thenReturn("http://author/content/test/page");

        mailTemplateStatic = mockStatic(MailTemplate.class);
        mailTemplateStatic.when(() -> MailTemplate.create(anyString(), any(Session.class))).thenReturn(mailTemplate);
    }

    @AfterEach
    void tearDown() {
        if (mailTemplateStatic != null) {
            mailTemplateStatic.close();
        }
    }

    @Test
    void testExecute_AssignmentNotification_SendsEmail() throws Exception {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("templatePath=/etc/notification/email/tmvc/workflow-notification.txt, reviewers=selfPeerReviewer");
        when(workflowMetaData.get("selfPeerReviewer", String.class)).thenReturn("reviewer-user");

        setupUserAuthorizable("reviewer-user", "reviewer@test.com", "John", "Doe");
        setupMessageGatewayWithEmail();

        process.execute(workItem, workflowSession, processArgs);

        verify(messageGateway).send(any(HtmlEmail.class));
    }

    @Test
    void testExecute_FeedbackNotification_UsesGeneralInboxUrl() throws Exception {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("templatePath=/etc/notification/email/tmvc/workflow-notification.txt, reviewers=initiator, notificationType=feedback");
        when(workflowMetaData.get("initiator", String.class)).thenReturn(null);
        when(workflowDataMetaData.get("initiator", String.class)).thenReturn(null);
        when(workflow.getInitiator()).thenReturn("author-user");

        setupUserAuthorizable("author-user", "author@test.com", "Jane", "Smith");
        setupMessageGatewayWithEmail();

        process.execute(workItem, workflowSession, processArgs);

        verify(messageGateway).send(any(HtmlEmail.class));
    }

    @Test
    void testExecute_NoReviewerIdsFound_ReturnsEarly() throws Exception {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("templatePath=/etc/notification/email/tmvc/workflow-notification.txt, reviewers=selfPeerReviewer");
        when(workflowMetaData.get("selfPeerReviewer", String.class)).thenReturn(null);
        when(workflowDataMetaData.get("selfPeerReviewer", String.class)).thenReturn(null);

        process.execute(workItem, workflowSession, processArgs);

        verify(resolverFactory, never()).getServiceResourceResolver(anyMap());
    }

    @Test
    void testExecute_EmptyReviewers_ReturnsEarly() throws Exception {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("templatePath=/etc/notification/email/tmvc/workflow-notification.txt, reviewers=");

        process.execute(workItem, workflowSession, processArgs);

        verify(resolverFactory, never()).getServiceResourceResolver(anyMap());
    }

    @Test
    void testExecute_InitiatorFallback() throws Exception {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("templatePath=/etc/notification/email/tmvc/workflow-notification.txt, reviewers=initiator");
        when(workflowMetaData.get("initiator", String.class)).thenReturn(null);
        when(workflowDataMetaData.get("initiator", String.class)).thenReturn(null);
        when(workflow.getInitiator()).thenReturn("initiator-user");

        setupUserAuthorizable("initiator-user", "initiator@test.com", "Init", "User");
        setupMessageGatewayWithEmail();

        process.execute(workItem, workflowSession, processArgs);

        verify(messageGateway).send(any(HtmlEmail.class));
    }

    @Test
    void testExecute_ReviewerFromWorkflowDataMetadata() throws Exception {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("templatePath=/etc/notification/email/tmvc/workflow-notification.txt, reviewers=qaReviewer");
        when(workflowMetaData.get("qaReviewer", String.class)).thenReturn(null);
        when(workflowDataMetaData.get("qaReviewer", String.class)).thenReturn("qa-user");

        setupUserAuthorizable("qa-user", "qa@test.com", "QA", "Tester");
        setupMessageGatewayWithEmail();

        process.execute(workItem, workflowSession, processArgs);

        verify(messageGateway).send(any(HtmlEmail.class));
    }

    @Test
    void testExecute_CommentsFromWorkflowMetadata() throws Exception {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("templatePath=/etc/notification/email/tmvc/workflow-notification.txt, reviewers=selfPeerReviewer");
        when(workflowMetaData.get("selfPeerReviewer", String.class)).thenReturn("reviewer-user");
        when(workflowMetaData.get("comments", String.class)).thenReturn("Please review ASAP");

        setupUserAuthorizable("reviewer-user", "reviewer@test.com", "John", "Doe");
        setupMessageGatewayWithEmail();

        process.execute(workItem, workflowSession, processArgs);

        verify(messageGateway).send(any(HtmlEmail.class));
    }

    @Test
    void testExecute_CommentsFallbackFromWorkflowData() throws Exception {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("templatePath=/etc/notification/email/tmvc/workflow-notification.txt, reviewers=selfPeerReviewer");
        when(workflowMetaData.get("selfPeerReviewer", String.class)).thenReturn("reviewer-user");
        when(workflowMetaData.get("comments", String.class)).thenReturn(null);
        when(workflowDataMetaData.get("comment", String.class)).thenReturn("Feedback from reviewer");

        setupUserAuthorizable("reviewer-user", "reviewer@test.com", "John", "Doe");
        setupMessageGatewayWithEmail();

        process.execute(workItem, workflowSession, processArgs);

        verify(messageGateway).send(any(HtmlEmail.class));
    }

    @Test
    void testExecute_LoginException_ThrowsWorkflowException() throws Exception {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("templatePath=/etc/notification/email/tmvc/workflow-notification.txt, reviewers=selfPeerReviewer");
        when(workflowMetaData.get("selfPeerReviewer", String.class)).thenReturn("reviewer-user");
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenThrow(new LoginException("Login failed"));

        org.junit.jupiter.api.Assertions.assertThrows(WorkflowException.class, () -> {
            process.execute(workItem, workflowSession, processArgs);
        });
    }

    @Test
    void testExecute_NullGateway_DoesNotThrow() throws Exception {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("templatePath=/etc/notification/email/tmvc/workflow-notification.txt, reviewers=selfPeerReviewer");
        when(workflowMetaData.get("selfPeerReviewer", String.class)).thenReturn("reviewer-user");

        setupUserAuthorizable("reviewer-user", "reviewer@test.com", "John", "Doe");
        when(messageGatewayService.getGateway(HtmlEmail.class)).thenReturn(null);

        process.execute(workItem, workflowSession, processArgs);

        verify(messageGateway, never()).send(any(HtmlEmail.class));
    }

    @Test
    void testExecute_NullMailTemplate_DoesNotThrow() throws Exception {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("templatePath=/etc/notification/email/tmvc/workflow-notification.txt, reviewers=selfPeerReviewer");
        when(workflowMetaData.get("selfPeerReviewer", String.class)).thenReturn("reviewer-user");

        setupUserAuthorizable("reviewer-user", "reviewer@test.com", "John", "Doe");
        when(messageGatewayService.getGateway(HtmlEmail.class)).thenReturn(messageGateway);
        mailTemplateStatic.when(() -> MailTemplate.create(anyString(), any(Session.class))).thenReturn(null);

        process.execute(workItem, workflowSession, processArgs);

        verify(messageGateway, never()).send(any(HtmlEmail.class));
    }

    @Test
    void testExecute_NoEmailResolved_DoesNotSend() throws Exception {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("templatePath=/etc/notification/email/tmvc/workflow-notification.txt, reviewers=selfPeerReviewer");
        when(workflowMetaData.get("selfPeerReviewer", String.class)).thenReturn("reviewer-user");

        when(userManager.getAuthorizable("reviewer-user")).thenReturn(null);

        process.execute(workItem, workflowSession, processArgs);

        verify(messageGatewayService, never()).getGateway(any());
    }

    @Test
    void testExecute_GroupReviewer_ResolvesMembers() throws Exception {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("templatePath=/etc/notification/email/tmvc/workflow-notification.txt, reviewers=selfPeerReviewer");
        when(workflowMetaData.get("selfPeerReviewer", String.class)).thenReturn("reviewer-group");

        Group group = mock(Group.class);
        when(userManager.getAuthorizable("reviewer-group")).thenReturn(group);
        when(group.isGroup()).thenReturn(true);

        Authorizable member = mock(Authorizable.class);
        when(member.isGroup()).thenReturn(false);
        Value emailValue = mock(Value.class);
        when(emailValue.getString()).thenReturn("member@test.com");
        when(member.getProperty("./profile/email")).thenReturn(new Value[]{emailValue});
        Value givenNameValue = mock(Value.class);
        when(givenNameValue.getString()).thenReturn("Member");
        when(member.getProperty("./profile/givenName")).thenReturn(new Value[]{givenNameValue});
        when(member.getProperty("./profile/familyName")).thenReturn(null);

        @SuppressWarnings("unchecked")
        Iterator<Authorizable> memberIterator = mock(Iterator.class);
        when(memberIterator.hasNext()).thenReturn(true, false);
        when(memberIterator.next()).thenReturn(member);
        when(group.getMembers()).thenReturn(memberIterator);

        setupMessageGatewayWithEmail();

        process.execute(workItem, workflowSession, processArgs);

        verify(messageGateway).send(any(HtmlEmail.class));
    }

    @Test
    void testExecute_DefaultTemplatePath_UsedWhenNotSpecified() throws Exception {
        when(processArgs.get("PROCESS_ARGS", "")).thenReturn("reviewers=selfPeerReviewer");
        when(workflowMetaData.get("selfPeerReviewer", String.class)).thenReturn("reviewer-user");

        setupUserAuthorizable("reviewer-user", "reviewer@test.com", "John", "Doe");
        setupMessageGatewayWithEmail();

        process.execute(workItem, workflowSession, processArgs);

        verify(messageGateway).send(any(HtmlEmail.class));
    }

    @Test
    void testExecute_StageEnvironment() throws Exception {
        Set<String> runModes = new HashSet<>();
        runModes.add("stage");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);

        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("templatePath=/etc/notification/email/tmvc/workflow-notification.txt, reviewers=selfPeerReviewer");
        when(workflowMetaData.get("selfPeerReviewer", String.class)).thenReturn("reviewer-user");

        setupUserAuthorizable("reviewer-user", "reviewer@test.com", "John", "Doe");
        setupMessageGatewayWithEmail();

        process.execute(workItem, workflowSession, processArgs);

        verify(messageGateway).send(any(HtmlEmail.class));
    }

    @Test
    void testExecute_ProdEnvironment() throws Exception {
        Set<String> runModes = new HashSet<>();
        runModes.add("prod");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);

        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("templatePath=/etc/notification/email/tmvc/workflow-notification.txt, reviewers=selfPeerReviewer");
        when(workflowMetaData.get("selfPeerReviewer", String.class)).thenReturn("reviewer-user");

        setupUserAuthorizable("reviewer-user", "reviewer@test.com", "John", "Doe");
        setupMessageGatewayWithEmail();

        process.execute(workItem, workflowSession, processArgs);

        verify(messageGateway).send(any(HtmlEmail.class));
    }

    @Test
    void testExecute_UserWithNoEmail_NotIncluded() throws Exception {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("templatePath=/etc/notification/email/tmvc/workflow-notification.txt, reviewers=selfPeerReviewer");
        when(workflowMetaData.get("selfPeerReviewer", String.class)).thenReturn("reviewer-user");

        when(userManager.getAuthorizable("reviewer-user")).thenReturn(userAuthorizable);
        when(userAuthorizable.isGroup()).thenReturn(false);
        when(userAuthorizable.getProperty("./profile/email")).thenReturn(null);
        when(userAuthorizable.getProperty("./profile/givenName")).thenReturn(null);
        when(userAuthorizable.getProperty("./profile/familyName")).thenReturn(null);

        process.execute(workItem, workflowSession, processArgs);

        verify(messageGatewayService, never()).getGateway(any());
    }

    private void setupUserAuthorizable(String userId, String email, String givenName, String familyName) throws Exception {
        when(userManager.getAuthorizable(userId)).thenReturn(userAuthorizable);
        when(userAuthorizable.isGroup()).thenReturn(false);

        Value emailValue = mock(Value.class);
        when(emailValue.getString()).thenReturn(email);
        when(userAuthorizable.getProperty("./profile/email")).thenReturn(new Value[]{emailValue});

        Value givenNameValue = mock(Value.class);
        when(givenNameValue.getString()).thenReturn(givenName);
        when(userAuthorizable.getProperty("./profile/givenName")).thenReturn(new Value[]{givenNameValue});

        Value familyNameValue = mock(Value.class);
        when(familyNameValue.getString()).thenReturn(familyName);
        when(userAuthorizable.getProperty("./profile/familyName")).thenReturn(new Value[]{familyNameValue});

        when(userAuthorizable.getID()).thenReturn(userId);
    }

    @SuppressWarnings("unchecked")
    private void setupMessageGatewayWithEmail() throws Exception {
        when(messageGatewayService.getGateway(HtmlEmail.class)).thenReturn(messageGateway);

        HtmlEmail htmlEmail = mock(HtmlEmail.class);
        when(mailTemplate.getEmail(any(Map.class), eq(HtmlEmail.class))).thenReturn(htmlEmail);
    }
}
