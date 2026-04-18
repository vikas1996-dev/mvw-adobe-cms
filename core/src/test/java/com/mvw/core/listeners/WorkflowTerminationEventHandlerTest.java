package com.mvw.core.listeners;

import static org.mockito.Mockito.*;

import java.util.Map;

import org.apache.sling.event.jobs.JobManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.adobe.granite.workflow.event.WorkflowEvent;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowNode;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WorkflowTerminationEventHandlerTest {

    @InjectMocks
    private WorkflowTerminationEventHandler handler;

    @Mock
    private JobManager jobManager;

    @Mock
    private WorkflowEvent workflowEvent;

    @Mock
    private WorkflowData workflowData;

    @Mock
    private WorkItem workItem;

    @Mock
    private WorkflowNode workflowNode;

    @Test
    void testHandleEvent_NonAbortedEvent() throws Exception {
        when(workflowEvent.getProperty(WorkflowEvent.EVENT_TYPE)).thenReturn(WorkflowEvent.WORKFLOW_COMPLETED_EVENT);

        handler.handleEvent(workflowEvent);

        verify(jobManager, never()).addJob(anyString(), anyMap());
    }

    @Test
    void testHandleEvent_AbortedEvent_EnqueuesCleanupJob() throws Exception {
        when(workflowEvent.getProperty(WorkflowEvent.EVENT_TYPE)).thenReturn(WorkflowEvent.WORKFLOW_ABORTED_EVENT);
        when(workflowEvent.getProperty(WorkflowEvent.WORKFLOW_INSTANCE_ID)).thenReturn("/var/workflow/instances/test");
        when(workflowEvent.getProperty(WorkflowEvent.WORKFLOW_NAME)).thenReturn("Test Workflow");
        when(workflowEvent.getProperty(WorkflowEvent.WORKFLOW_NODE)).thenReturn("node1");
        when(workflowEvent.getProperty(WorkflowEvent.USER)).thenReturn("admin");
        when(workflowEvent.getProperty(WorkflowTerminationEventHandler.PROP_PAYLOAD_PATH))
                .thenReturn("/content/dam/test.pdf");
        when(workflowEvent.getProperty(WorkflowEvent.WORK_DATA)).thenReturn(null);
        when(workflowEvent.getProperty(WorkflowEvent.WORK_ITEM)).thenReturn(null);

        handler.handleEvent(workflowEvent);

        ArgumentCaptor<Map<String, Object>> propsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(jobManager).addJob(eq(WorkflowTerminationEventHandler.WORKFLOW_TERMINATION_JOB_TOPIC),
                propsCaptor.capture());
        Map<String, Object> props = propsCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals("/var/workflow/instances/test",
                props.get(WorkflowTerminationEventHandler.PROP_WORKFLOW_INSTANCE_ID));
        org.junit.jupiter.api.Assertions.assertEquals("admin",
                props.get(WorkflowTerminationEventHandler.PROP_WORKFLOW_USER));
        org.junit.jupiter.api.Assertions.assertEquals("/content/dam/test.pdf",
                props.get(WorkflowTerminationEventHandler.PROP_PAYLOAD_PATH));
    }

    @Test
    void testHandleEvent_AbortedEvent_WithWorkItem_EnqueuesCleanupJob() throws Exception {
        when(workflowEvent.getProperty(WorkflowEvent.EVENT_TYPE)).thenReturn(WorkflowEvent.WORKFLOW_ABORTED_EVENT);
        when(workflowEvent.getProperty(WorkflowEvent.WORKFLOW_INSTANCE_ID)).thenReturn("/var/workflow/instances/test");
        when(workflowEvent.getProperty(WorkflowEvent.WORKFLOW_NAME)).thenReturn("Test Workflow");
        when(workflowEvent.getProperty(WorkflowEvent.WORKFLOW_NODE)).thenReturn("node1");
        when(workflowEvent.getProperty(WorkflowEvent.USER)).thenReturn("admin");
        when(workflowEvent.getProperty(WorkflowTerminationEventHandler.PROP_PAYLOAD_PATH))
                .thenReturn("/content/dam/test.pdf");
        when(workflowEvent.getProperty(WorkflowEvent.WORK_DATA)).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn("/content/dam/test.pdf");
        when(workflowData.getPayloadType()).thenReturn("JCR_PATH");
        when(workflowEvent.getProperty(WorkflowEvent.WORK_ITEM)).thenReturn(workItem);
        when(workItem.getId()).thenReturn("workitem-123");
        when(workItem.getNode()).thenReturn(workflowNode);
        when(workflowNode.getTitle()).thenReturn("Test Node");

        handler.handleEvent(workflowEvent);

        verify(jobManager).addJob(eq(WorkflowTerminationEventHandler.WORKFLOW_TERMINATION_JOB_TOPIC), anyMap());
    }
}
