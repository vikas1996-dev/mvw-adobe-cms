package com.mvw.core.listeners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer.JobResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WorkflowTerminationJobConsumerTest {

    @InjectMocks
    private WorkflowTerminationJobConsumer consumer;

    @Mock
    private ResourceResolverFactory resolverFactory;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Job job;

    @Mock
    private Resource instanceResource;

    @Mock
    private Resource metaDataResource;

    @Mock
    private Resource payloadResource;

    @Mock
    private Resource damMetaDataResource;

    @Mock
    private ModifiableValueMap metaMvm;

    @Mock
    private ModifiableValueMap damMvm;

    @Mock
    private ValueMap payloadValueMap;

    @Test
    void testProcess_MissingWorkflowInstanceId() {
        when(job.getProperty(WorkflowTerminationEventHandler.PROP_WORKFLOW_INSTANCE_ID, String.class)).thenReturn(null);

        JobResult result = consumer.process(job);

        assertEquals(JobResult.CANCEL, result);
    }

    @Test
    void testProcess_Success() throws Exception {
        when(job.getProperty(WorkflowTerminationEventHandler.PROP_WORKFLOW_INSTANCE_ID, String.class))
                .thenReturn("/var/workflow/instances/test");
        when(job.getProperty(WorkflowTerminationEventHandler.PROP_PAYLOAD_PATH, String.class))
                .thenReturn("/content/dam/test.pdf");
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resourceResolver);
        when(resourceResolver.getResource("/var/workflow/instances/test")).thenReturn(instanceResource);
        when(resourceResolver.getResource("/var/workflow/instances/test/data/metaData")).thenReturn(metaDataResource);
        when(metaDataResource.adaptTo(ModifiableValueMap.class)).thenReturn(metaMvm);
        when(metaMvm.containsKey("edtTime")).thenReturn(true);
        when(metaMvm.containsKey("scheduled_publish_date")).thenReturn(true);
        when(metaMvm.containsKey("utc_time")).thenReturn(true);
        when(metaDataResource.getPath()).thenReturn("/var/workflow/instances/test/data/metaData");
        when(resourceResolver.getResource("/content/dam/test.pdf/jcr:content/metadata")).thenReturn(damMetaDataResource);
        when(damMetaDataResource.adaptTo(ModifiableValueMap.class)).thenReturn(damMvm);
        when(damMvm.containsKey("edtTime")).thenReturn(true);
        when(damMvm.containsKey("scheduled_publish_date")).thenReturn(true);
        when(damMvm.containsKey("utc_time")).thenReturn(true);
        when(damMvm.containsKey("userId")).thenReturn(true);
        when(damMetaDataResource.getPath()).thenReturn("/content/dam/test.pdf/jcr:content/metadata");

        JobResult result = consumer.process(job);

        assertEquals(JobResult.OK, result);
        verify(metaMvm).remove("edtTime");
        verify(metaMvm).remove("scheduled_publish_date");
        verify(metaMvm).remove("utc_time");
        verify(damMvm).remove("edtTime");
        verify(damMvm).remove("scheduled_publish_date");
        verify(damMvm).remove("utc_time");
        verify(damMvm).remove("userId");
        verify(resourceResolver).commit();
        verify(resourceResolver).close();
    }

    @Test
    void testProcess_NoWorkflowResource() throws Exception {
        when(job.getProperty(WorkflowTerminationEventHandler.PROP_WORKFLOW_INSTANCE_ID, String.class))
                .thenReturn("/var/workflow/instances/test");
        when(job.getProperty(WorkflowTerminationEventHandler.PROP_PAYLOAD_PATH, String.class))
                .thenReturn("/content/dam/test.pdf");
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resourceResolver);
        when(resourceResolver.getResource("/var/workflow/instances/test")).thenReturn(null);

        JobResult result = consumer.process(job);

        assertEquals(JobResult.OK, result);
        verify(resourceResolver, never()).commit();
        verify(resourceResolver, times(1)).close();
    }

    @Test
    void testProcess_LoginException() throws Exception {
        when(job.getProperty(WorkflowTerminationEventHandler.PROP_WORKFLOW_INSTANCE_ID, String.class))
                .thenReturn("/var/workflow/instances/test");
        when(job.getProperty(WorkflowTerminationEventHandler.PROP_PAYLOAD_PATH, String.class))
                .thenReturn("/content/dam/test.pdf");
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenThrow(new LoginException("Login failed"));

        JobResult result = consumer.process(job);

        assertEquals(JobResult.FAILED, result);
    }
}
