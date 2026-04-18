package com.mvw.core.schedulers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.mvw.core.services.impl.MvwGoverningRegistrationBoxes;
import java.util.List;
import java.util.Set;
import javax.jcr.Session;
import org.apache.commons.mail.HtmlEmail;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer.JobResult;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmailJobConsumerTest {

    @InjectMocks
    private EmailJobConsumer consumer;

    @Mock
    private ResourceResolverFactory resolverFactory;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private MessageGatewayService messageGatewayService;

    @Mock
    private SlingSettingsService slingSettingsService;

    @Mock
    private MvwGoverningRegistrationBoxes registrationBoxes;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Session session;

    @Mock
    private Job job;

    @Mock
    private Query query;

    @Mock
    private SearchResult searchResult;

    @Mock
    private Hit hit;

    @Mock
    private Resource workflowResource;

    @Mock
    private Resource metaResource;

    @Mock
    private Resource payloadResource;

    @Mock
    private ValueMap workflowValueMap;

    @Mock
    private ValueMap metaValueMap;

    @Mock
    private ValueMap payloadValueMap;

    @Mock
    private MessageGateway<HtmlEmail> messageGateway;

    @Mock
    private MailTemplate mailTemplate;

    @Mock
    private HtmlEmail htmlEmail;

    private MockedStatic<MailTemplate> mailTemplateStatic;

    @BeforeEach
    void setUp() throws Exception {
        when(registrationBoxes.getMarriottsRegistrationBox()).thenReturn("marriott-box@test.com");
        when(registrationBoxes.getHyattRegistrationBox()).thenReturn("hyatt-box@test.com");
        when(registrationBoxes.getFromAddress()).thenReturn("sender@test.com");

        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(session.getUserID()).thenReturn("service-user");
        when(session.hasPermission(anyString(), anyString())).thenReturn(true);

        when(queryBuilder.createQuery(any(PredicateGroup.class), eq(session))).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        when(messageGatewayService.getGateway(HtmlEmail.class)).thenReturn(messageGateway);
        when(slingSettingsService.getRunModes()).thenReturn(Set.of("author", "qa"));

        mailTemplateStatic = org.mockito.Mockito.mockStatic(MailTemplate.class);
        mailTemplateStatic.when(() -> MailTemplate.create(anyString(), eq(session))).thenReturn(mailTemplate);
        when(mailTemplate.getEmail(anyMap(), eq(HtmlEmail.class))).thenReturn(htmlEmail);

        when(workflowResource.getValueMap()).thenReturn(workflowValueMap);
        when(metaResource.getValueMap()).thenReturn(metaValueMap);
        when(payloadResource.getValueMap()).thenReturn(payloadValueMap);
        when(hit.getPath()).thenReturn("/var/workflow/instances/test");
        when(resourceResolver.getResource("/var/workflow/instances/test")).thenReturn(workflowResource);
        when(resourceResolver.getResource("/var/workflow/instances/test/data/metaData")).thenReturn(metaResource);
        when(resourceResolver.getResource("/var/workflow/instances/test/data/payload")).thenReturn(payloadResource);
        when(searchResult.getHits()).thenReturn(List.of(hit));
        when(workflowValueMap.get("status", "")).thenReturn("RUNNING");
        when(metaValueMap.get("title", String.class)).thenReturn("Document One");
        when(metaValueMap.get("userId", String.class)).thenReturn("author1");
        when(metaValueMap.get("scheduled_publish_date", String.class)).thenReturn("2026-04-13 10:00 AM");
        when(metaValueMap.get("timeSharePlan", String.class)).thenReturn("Marriott Vacation Club");
        when(metaValueMap.get("absoluteTime")).thenReturn(System.currentTimeMillis() + 60L * 60L * 60L * 1000L);
    }

    @AfterEach
    void tearDown() {
        if (mailTemplateStatic != null) {
            mailTemplateStatic.close();
        }
    }

    @Test
    void testProcess_SendsMarriottEmailUsingBulkPublishTemplate() throws Exception {
        when(payloadValueMap.get("path", String.class))
                .thenReturn("/content/dam/legal/marriottvacationclubs/test.pdf");

        JobResult result = consumer.process(job);

        assertEquals(JobResult.OK, result);
        ArgumentCaptor<java.util.Map<String, String>> paramsCaptor = ArgumentCaptor.forClass(java.util.Map.class);
        verify(mailTemplate).getEmail(paramsCaptor.capture(), eq(HtmlEmail.class));
        verify(htmlEmail).setFrom("sender@test.com");
        verify(htmlEmail).addTo("marriott-box@test.com");
        verify(messageGateway).send(htmlEmail);
        assertEquals("QA", paramsCaptor.getValue().get("environmentName"));
        assertTrue(paramsCaptor.getValue().get("documentRows").contains("Document One"));
        assertTrue(paramsCaptor.getValue().get("documentRows").contains("author1"));
        assertTrue(paramsCaptor.getValue().get("documentRows").contains("2026-04-13 10:00 AM"));
        assertTrue(paramsCaptor.getValue().get("documentRows").contains("Marriott Vacation Club"));
    }

    @Test
    void testProcess_SendsHyattEmailToHyattRegistrationBox() throws Exception {
        when(payloadValueMap.get("path", String.class))
                .thenReturn("/content/dam/legal/hyattvacationclub/test.pdf");
        when(metaValueMap.get("timeSharePlan", String.class)).thenReturn("Hyatt Vacation Club");

        JobResult result = consumer.process(job);

        assertEquals(JobResult.OK, result);
        verify(htmlEmail).addTo("hyatt-box@test.com");
        verify(messageGateway).send(htmlEmail);
    }

    @Test
    void testProcess_BlankMarriottRecipientSkipsEmail() throws Exception {
        when(registrationBoxes.getMarriottsRegistrationBox()).thenReturn(" ");
        when(payloadValueMap.get("path", String.class))
                .thenReturn("/content/dam/legal/marriottvacationclubs/test.pdf");

        JobResult result = consumer.process(job);

        assertEquals(JobResult.OK, result);
        verify(mailTemplate, never()).getEmail(anyMap(), eq(HtmlEmail.class));
        verify(messageGateway, never()).send(any(HtmlEmail.class));
    }

    @Test
    void testProcess_LoginException() throws Exception {
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenThrow(new LoginException("Login failed"));

        JobResult result = consumer.process(job);

        assertEquals(JobResult.FAILED, result);
    }

    @Test
    void testProcess_NullSession() throws Exception {
        when(resourceResolver.adaptTo(Session.class)).thenReturn(null);

        JobResult result = consumer.process(job);

        assertEquals(JobResult.FAILED, result);
    }
}
