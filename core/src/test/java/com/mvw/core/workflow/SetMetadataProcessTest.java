package com.mvw.core.workflow;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SetMetadataProcessTest {

    private SetMetadataProcess process;

    @Mock private WorkItem workItem;
    @Mock private WorkflowSession workflowSession;
    @Mock private MetaDataMap processArgs;
    @Mock private WorkflowData workflowData;
    @Mock private MetaDataMap workflowDataMap;

    @BeforeEach
    void setUp() {
        process = new SetMetadataProcess();
        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getMetaDataMap()).thenReturn(workflowDataMap);
    }

    @Test
    void testSingleKeyValue() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", "")).thenReturn("continueLoop=false");

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowDataMap).put("continueLoop", "false");
    }

    @Test
    void testMultipleKeyValues() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", "")).thenReturn("continueLoop=true, reviewerName=john");

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowDataMap).put("continueLoop", "true");
        verify(workflowDataMap).put("reviewerName", "john");
    }

    @Test
    void testBlankArgs_Skips() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", "")).thenReturn("");

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowDataMap, never()).put(any(), any());
    }

    @Test
    void testNullArgs_Skips() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", "")).thenReturn(null);

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowDataMap, never()).put(any(), any());
    }

    @Test
    void testWhitespaceOnlyArgs_Skips() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", "")).thenReturn("   ");

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowDataMap, never()).put(any(), any());
    }

    @Test
    void testTrimsKeysAndValues() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", "")).thenReturn("  continueLoop = true  ");

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowDataMap).put("continueLoop", "true");
    }

    @Test
    void testMalformedPair_Skipped() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", "")).thenReturn("noequalssign, continueLoop=false");

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowDataMap).put("continueLoop", "false");
        verify(workflowDataMap, times(1)).put(any(), any());
    }

    @Test
    void testValueContainingEquals() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", "")).thenReturn("expression=a=b");

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowDataMap).put("expression", "a=b");
    }

    @Test
    void testDuplicateKeys_KeepsFirst() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", "")).thenReturn("continueLoop=true, continueLoop=false");

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowDataMap).put("continueLoop", "true");
        verify(workflowDataMap, times(1)).put(any(), any());
    }
}
