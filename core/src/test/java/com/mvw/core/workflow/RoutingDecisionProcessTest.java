package com.mvw.core.workflow;

import static org.junit.jupiter.api.Assertions.*;
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
class RoutingDecisionProcessTest {

    private RoutingDecisionProcess process;

    @Mock
    private WorkItem workItem;

    @Mock
    private WorkflowSession workflowSession;

    @Mock
    private MetaDataMap metaDataMap;

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
    private MetaDataMap historyMetaData;

    @Mock
    private WorkflowData historyWorkflowData;

    @Mock
    private MetaDataMap historyWorkflowDataMeta;

    @BeforeEach
    void setUp() {
        process = new RoutingDecisionProcess();
        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getMetaDataMap()).thenReturn(workflowDataMap);
        when(workItem.getWorkflow()).thenReturn(workflow);
    }

    @Test
    void testExecute_ReviewerFoundInHistory() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn("reviewer=qaReviewer");

        List<HistoryItem> history = new ArrayList<>();
        history.add(historyItem);
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        when(historyItem.getWorkItem()).thenReturn(historyWorkItem);
        when(historyWorkItem.getMetaDataMap()).thenReturn(historyMetaData);
        when(historyWorkItem.getWorkflowData()).thenReturn(historyWorkflowData);
        when(historyWorkflowData.getMetaDataMap()).thenReturn(historyWorkflowDataMeta);
        when(historyMetaData.containsKey("qaReviewer")).thenReturn(true);
        when(historyMetaData.get("qaReviewer", String.class)).thenReturn("qa-user");
        when(historyWorkflowDataMeta.containsKey("qaReviewer")).thenReturn(false);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("qaReviewer", "qa-user");
        verify(workflowDataMap).put("reviewer", true);
    }

    @Test
    void testExecute_ReviewerNotFoundInHistory() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn("reviewer=qaReviewer");

        List<HistoryItem> history = new ArrayList<>();
        history.add(historyItem);
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        when(historyItem.getWorkItem()).thenReturn(historyWorkItem);
        when(historyWorkItem.getMetaDataMap()).thenReturn(historyMetaData);
        when(historyWorkItem.getWorkflowData()).thenReturn(historyWorkflowData);
        when(historyWorkflowData.getMetaDataMap()).thenReturn(historyWorkflowDataMeta);
        when(historyMetaData.containsKey("qaReviewer")).thenReturn(false);
        when(historyWorkflowDataMeta.containsKey("qaReviewer")).thenReturn(false);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("reviewer", false);
    }

    @Test
    void testExecute_EmptyHistory() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn("reviewer=qaReviewer");
        
        List<HistoryItem> history = new ArrayList<>();
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("reviewer", false);
    }

    @Test
    void testExecute_BlankPropertyName() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn("reviewer=");

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("reviewer", false);
    }

    @Test
    void testExecute_NullProcessArgs() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn(null);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("reviewer", false);
    }

    @Test
    void testExecute_EmptyProcessArgs() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn("");

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("reviewer", false);
    }

    @Test
    void testExecute_ReviewerValueBlank() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn("reviewer=qaReviewer");

        List<HistoryItem> history = new ArrayList<>();
        history.add(historyItem);
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        when(historyItem.getWorkItem()).thenReturn(historyWorkItem);
        when(historyWorkItem.getMetaDataMap()).thenReturn(historyMetaData);
        when(historyWorkItem.getWorkflowData()).thenReturn(historyWorkflowData);
        when(historyWorkflowData.getMetaDataMap()).thenReturn(historyWorkflowDataMeta);
        when(historyMetaData.containsKey("qaReviewer")).thenReturn(true);
        when(historyMetaData.get("qaReviewer", String.class)).thenReturn("  ");
        when(historyWorkflowDataMeta.containsKey("qaReviewer")).thenReturn(false);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("reviewer", false);
    }

    @Test
    void testExecute_MultipleHistoryItems() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn("reviewer=qaReviewer");

        HistoryItem historyItem2 = mock(HistoryItem.class);
        WorkItem historyWorkItem2 = mock(WorkItem.class);
        MetaDataMap historyMetaData2 = mock(MetaDataMap.class);
        WorkflowData historyWorkflowData2 = mock(WorkflowData.class);
        MetaDataMap historyWorkflowDataMeta2 = mock(MetaDataMap.class);

        List<HistoryItem> history = new ArrayList<>();
        history.add(historyItem);
        history.add(historyItem2);
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        when(historyItem2.getWorkItem()).thenReturn(historyWorkItem2);
        when(historyWorkItem2.getMetaDataMap()).thenReturn(historyMetaData2);
        when(historyWorkItem2.getWorkflowData()).thenReturn(historyWorkflowData2);
        when(historyWorkflowData2.getMetaDataMap()).thenReturn(historyWorkflowDataMeta2);
        when(historyMetaData2.containsKey("qaReviewer")).thenReturn(false);
        when(historyWorkflowDataMeta2.containsKey("qaReviewer")).thenReturn(false);

        when(historyItem.getWorkItem()).thenReturn(historyWorkItem);
        when(historyWorkItem.getMetaDataMap()).thenReturn(historyMetaData);
        when(historyWorkItem.getWorkflowData()).thenReturn(historyWorkflowData);
        when(historyWorkflowData.getMetaDataMap()).thenReturn(historyWorkflowDataMeta);
        when(historyMetaData.containsKey("qaReviewer")).thenReturn(true);
        when(historyMetaData.get("qaReviewer", String.class)).thenReturn("qa-user");
        when(historyWorkflowDataMeta.containsKey("qaReviewer")).thenReturn(false);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("qaReviewer", "qa-user");
        verify(workflowDataMap).put("reviewer", true);
    }

    @Test
    void testExecute_InvalidArgsFormat() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn("invalid-format");

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("reviewer", false);
    }

    @Test
    void testExecute_ReviewerFoundInWorkflowDataMetadata() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn("reviewer=qaReviewer");

        List<HistoryItem> history = new ArrayList<>();
        history.add(historyItem);
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        when(historyItem.getWorkItem()).thenReturn(historyWorkItem);
        when(historyWorkItem.getMetaDataMap()).thenReturn(historyMetaData);
        when(historyWorkItem.getWorkflowData()).thenReturn(historyWorkflowData);
        when(historyWorkflowData.getMetaDataMap()).thenReturn(historyWorkflowDataMeta);

        when(historyMetaData.containsKey("qaReviewer")).thenReturn(false);
        when(historyWorkflowDataMeta.containsKey("qaReviewer")).thenReturn(true);
        when(historyWorkflowDataMeta.get("qaReviewer", String.class)).thenReturn("qa-user");

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("qaReviewer", "qa-user");
        verify(workflowDataMap).put("reviewer", true);
    }

    // ========================================================================
    // Per-gate routing tests for all 5 reviewer types
    // Each review gate independently checks its reviewer via history lookup.
    // ========================================================================

    @Test
    void testExecute_SelfPeerReviewerFoundInHistory() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn("reviewer=selfPeerReviewer");

        List<HistoryItem> history = new ArrayList<>();
        history.add(historyItem);
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        when(historyItem.getWorkItem()).thenReturn(historyWorkItem);
        when(historyWorkItem.getMetaDataMap()).thenReturn(historyMetaData);
        when(historyWorkItem.getWorkflowData()).thenReturn(historyWorkflowData);
        when(historyWorkflowData.getMetaDataMap()).thenReturn(historyWorkflowDataMeta);
        when(historyMetaData.containsKey("selfPeerReviewer")).thenReturn(true);
        when(historyMetaData.get("selfPeerReviewer", String.class)).thenReturn("peer-user");
        when(historyWorkflowDataMeta.containsKey("selfPeerReviewer")).thenReturn(false);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("selfPeerReviewer", "peer-user");
        verify(workflowDataMap).put("reviewer", true);
    }

    @Test
    void testExecute_SelfPeerReviewerNotFound() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn("reviewer=selfPeerReviewer");

        List<HistoryItem> history = new ArrayList<>();
        history.add(historyItem);
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        when(historyItem.getWorkItem()).thenReturn(historyWorkItem);
        when(historyWorkItem.getMetaDataMap()).thenReturn(historyMetaData);
        when(historyWorkItem.getWorkflowData()).thenReturn(historyWorkflowData);
        when(historyWorkflowData.getMetaDataMap()).thenReturn(historyWorkflowDataMeta);
        when(historyMetaData.containsKey("selfPeerReviewer")).thenReturn(false);
        when(historyWorkflowDataMeta.containsKey("selfPeerReviewer")).thenReturn(false);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("reviewer", false);
    }

    @Test
    void testExecute_CopyProofreadReviewerFoundInHistory() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn("reviewer=copyProofreadReviewer");

        List<HistoryItem> history = new ArrayList<>();
        history.add(historyItem);
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        when(historyItem.getWorkItem()).thenReturn(historyWorkItem);
        when(historyWorkItem.getMetaDataMap()).thenReturn(historyMetaData);
        when(historyWorkItem.getWorkflowData()).thenReturn(historyWorkflowData);
        when(historyWorkflowData.getMetaDataMap()).thenReturn(historyWorkflowDataMeta);
        when(historyMetaData.containsKey("copyProofreadReviewer")).thenReturn(true);
        when(historyMetaData.get("copyProofreadReviewer", String.class)).thenReturn("copyedit-user");
        when(historyWorkflowDataMeta.containsKey("copyProofreadReviewer")).thenReturn(false);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("copyProofreadReviewer", "copyedit-user");
        verify(workflowDataMap).put("reviewer", true);
    }

    @Test
    void testExecute_CopyProofreadReviewerNotFound() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn("reviewer=copyProofreadReviewer");

        List<HistoryItem> history = new ArrayList<>();
        history.add(historyItem);
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        when(historyItem.getWorkItem()).thenReturn(historyWorkItem);
        when(historyWorkItem.getMetaDataMap()).thenReturn(historyMetaData);
        when(historyWorkItem.getWorkflowData()).thenReturn(historyWorkflowData);
        when(historyWorkflowData.getMetaDataMap()).thenReturn(historyWorkflowDataMeta);
        when(historyMetaData.containsKey("copyProofreadReviewer")).thenReturn(false);
        when(historyWorkflowDataMeta.containsKey("copyProofreadReviewer")).thenReturn(false);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("reviewer", false);
    }

    @Test
    void testExecute_BusinessReviewerFoundInHistory() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn("reviewer=businessReviewer");

        List<HistoryItem> history = new ArrayList<>();
        history.add(historyItem);
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        when(historyItem.getWorkItem()).thenReturn(historyWorkItem);
        when(historyWorkItem.getMetaDataMap()).thenReturn(historyMetaData);
        when(historyWorkItem.getWorkflowData()).thenReturn(historyWorkflowData);
        when(historyWorkflowData.getMetaDataMap()).thenReturn(historyWorkflowDataMeta);
        when(historyMetaData.containsKey("businessReviewer")).thenReturn(true);
        when(historyMetaData.get("businessReviewer", String.class)).thenReturn("biz-user");
        when(historyWorkflowDataMeta.containsKey("businessReviewer")).thenReturn(false);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("businessReviewer", "biz-user");
        verify(workflowDataMap).put("reviewer", true);
    }

    @Test
    void testExecute_BusinessReviewerNotFound() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn("reviewer=businessReviewer");

        List<HistoryItem> history = new ArrayList<>();
        history.add(historyItem);
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        when(historyItem.getWorkItem()).thenReturn(historyWorkItem);
        when(historyWorkItem.getMetaDataMap()).thenReturn(historyMetaData);
        when(historyWorkItem.getWorkflowData()).thenReturn(historyWorkflowData);
        when(historyWorkflowData.getMetaDataMap()).thenReturn(historyWorkflowDataMeta);
        when(historyMetaData.containsKey("businessReviewer")).thenReturn(false);
        when(historyWorkflowDataMeta.containsKey("businessReviewer")).thenReturn(false);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("reviewer", false);
    }

    @Test
    void testExecute_LegalReviewerFoundInHistory() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn("reviewer=legalReviewer");

        List<HistoryItem> history = new ArrayList<>();
        history.add(historyItem);
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        when(historyItem.getWorkItem()).thenReturn(historyWorkItem);
        when(historyWorkItem.getMetaDataMap()).thenReturn(historyMetaData);
        when(historyWorkItem.getWorkflowData()).thenReturn(historyWorkflowData);
        when(historyWorkflowData.getMetaDataMap()).thenReturn(historyWorkflowDataMeta);
        when(historyMetaData.containsKey("legalReviewer")).thenReturn(true);
        when(historyMetaData.get("legalReviewer", String.class)).thenReturn("legal-user");
        when(historyWorkflowDataMeta.containsKey("legalReviewer")).thenReturn(false);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("legalReviewer", "legal-user");
        verify(workflowDataMap).put("reviewer", true);
    }

    @Test
    void testExecute_LegalReviewerNotFound() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn("reviewer=legalReviewer");

        List<HistoryItem> history = new ArrayList<>();
        history.add(historyItem);
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        when(historyItem.getWorkItem()).thenReturn(historyWorkItem);
        when(historyWorkItem.getMetaDataMap()).thenReturn(historyMetaData);
        when(historyWorkItem.getWorkflowData()).thenReturn(historyWorkflowData);
        when(historyWorkflowData.getMetaDataMap()).thenReturn(historyWorkflowDataMeta);
        when(historyMetaData.containsKey("legalReviewer")).thenReturn(false);
        when(historyWorkflowDataMeta.containsKey("legalReviewer")).thenReturn(false);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("reviewer", false);
    }

    @Test
    void testExecute_LegalReviewerFoundInWorkflowDataMeta() throws WorkflowException {
        when(metaDataMap.get("PROCESS_ARGS", "")).thenReturn("reviewer=legalReviewer");

        List<HistoryItem> history = new ArrayList<>();
        history.add(historyItem);
        when(workflowSession.getHistory(workflow)).thenReturn(history);

        when(historyItem.getWorkItem()).thenReturn(historyWorkItem);
        when(historyWorkItem.getMetaDataMap()).thenReturn(historyMetaData);
        when(historyWorkItem.getWorkflowData()).thenReturn(historyWorkflowData);
        when(historyWorkflowData.getMetaDataMap()).thenReturn(historyWorkflowDataMeta);
        when(historyMetaData.containsKey("legalReviewer")).thenReturn(false);
        when(historyWorkflowDataMeta.containsKey("legalReviewer")).thenReturn(true);
        when(historyWorkflowDataMeta.get("legalReviewer", String.class)).thenReturn("legal-user");

        process.execute(workItem, workflowSession, metaDataMap);

        verify(workflowDataMap).put("legalReviewer", "legal-user");
        verify(workflowDataMap).put("reviewer", true);
    }
}
