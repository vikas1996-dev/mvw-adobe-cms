package com.mvw.core.workflow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CopyReviewerActionProcessTest {

    private CopyReviewerActionProcess process;

    @Mock
    private WorkItem workItem;

    @Mock
    private WorkflowSession workflowSession;

    @Mock
    private MetaDataMap processArgs;

    @Mock
    private WorkflowData workflowData;

    @Mock
    private MetaDataMap workflowDataMap;

    @Mock
    private Workflow workflow;

    @Mock
    private HistoryItem historyItem;

    @Mock
    private WorkItem historyWorkItem;

    @Mock
    private MetaDataMap stepMetadata;

    @BeforeEach
    void setUp() {
        process = new CopyReviewerActionProcess();
        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getMetaDataMap()).thenReturn(workflowDataMap);
        when(workItem.getWorkflow()).thenReturn(workflow);
    }

    @Test
    void testExecute_ReviewerActionFound() throws WorkflowException {
        List<HistoryItem> history = new ArrayList<>();
        history.add(historyItem);
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        when(historyItem.getWorkItem()).thenReturn(historyWorkItem);
        when(historyWorkItem.getMetaDataMap()).thenReturn(stepMetadata);
        when(stepMetadata.containsKey("reviewerAction")).thenReturn(true);
        when(stepMetadata.get("reviewerAction", String.class)).thenReturn("feedback");
        when(stepMetadata.containsKey("comment")).thenReturn(false);

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowDataMap).put("reviewerAction", "feedback");
    }

    @Test
    void testExecute_ReviewerActionAndCommentFound() throws WorkflowException {
        List<HistoryItem> history = new ArrayList<>();
        history.add(historyItem);
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        when(historyItem.getWorkItem()).thenReturn(historyWorkItem);
        when(historyWorkItem.getMetaDataMap()).thenReturn(stepMetadata);
        when(stepMetadata.containsKey("reviewerAction")).thenReturn(true);
        when(stepMetadata.get("reviewerAction", String.class)).thenReturn("feedback");
        when(stepMetadata.containsKey("comment")).thenReturn(true);
        when(stepMetadata.get("comment", String.class)).thenReturn("Please fix the typo");

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowDataMap).put("reviewerAction", "feedback");
        verify(workflowDataMap).put("comment", "Please fix the typo");
    }

    @Test
    void testExecute_ReviewerActionNotFound() throws WorkflowException {
        List<HistoryItem> history = new ArrayList<>();
        history.add(historyItem);
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        when(historyItem.getWorkItem()).thenReturn(historyWorkItem);
        when(historyWorkItem.getMetaDataMap()).thenReturn(stepMetadata);
        when(stepMetadata.containsKey("reviewerAction")).thenReturn(false);

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowDataMap, never()).put(eq("reviewerAction"), any());
        verify(workflowDataMap, never()).put(eq("comment"), any());
    }

    @Test
    void testExecute_EmptyHistory() throws WorkflowException {
        List<HistoryItem> history = new ArrayList<>();
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowDataMap, never()).put(eq("reviewerAction"), any());
    }

    @Test
    void testExecute_MultipleHistoryItems_UsesMostRecent() throws WorkflowException {
        HistoryItem historyItem2 = mock(HistoryItem.class);
        WorkItem historyWorkItem2 = mock(WorkItem.class);
        MetaDataMap stepMetadata2 = mock(MetaDataMap.class);

        List<HistoryItem> history = new ArrayList<>();
        history.add(historyItem);
        history.add(historyItem2);
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        when(historyItem2.getWorkItem()).thenReturn(historyWorkItem2);
        when(historyWorkItem2.getMetaDataMap()).thenReturn(stepMetadata2);
        when(stepMetadata2.containsKey("reviewerAction")).thenReturn(true);
        when(stepMetadata2.get("reviewerAction", String.class)).thenReturn("approve");
        when(stepMetadata2.containsKey("comment")).thenReturn(false);

        when(historyItem.getWorkItem()).thenReturn(historyWorkItem);
        when(historyWorkItem.getMetaDataMap()).thenReturn(stepMetadata);
        when(stepMetadata.containsKey("reviewerAction")).thenReturn(true);
        when(stepMetadata.get("reviewerAction", String.class)).thenReturn("feedback");

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowDataMap).put("reviewerAction", "approve");
    }

    @Test
    void testExecute_ExceptionThrown() throws WorkflowException {
        when(workflowSession.getHistory(workflow)).thenThrow(new RuntimeException("Test error"));

        org.junit.jupiter.api.Assertions.assertThrows(WorkflowException.class, () -> {
            process.execute(workItem, workflowSession, processArgs);
        });
    }
}
