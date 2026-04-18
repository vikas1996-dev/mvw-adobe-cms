package com.mvw.core.workflow;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
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
import com.adobe.granite.workflow.exec.Route;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.model.WorkflowNode;
import com.adobe.granite.workflow.model.WorkflowTransition;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ConditionalGotoProcessTest {

    private ConditionalGotoProcess process;

    @Mock private WorkItem workItem;
    @Mock private WorkflowSession workflowSession;
    @Mock private MetaDataMap processArgs;
    @Mock private WorkflowData workflowData;
    @Mock private MetaDataMap workflowDataMap;
    @Mock private WorkflowNode currentNode;

    @Mock private Route forwardRoute;
    @Mock private Route backRouteTarget;
    @Mock private Route backRouteOther;

    @Mock private WorkflowTransition transitionTarget;
    @Mock private WorkflowTransition transitionOther;
    @Mock private WorkflowNode targetNode;
    @Mock private WorkflowNode otherNode;

    @BeforeEach
    void setUp() throws WorkflowException {
        process = new ConditionalGotoProcess();
        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getMetaDataMap()).thenReturn(workflowDataMap);
        when(workItem.getNode()).thenReturn(currentNode);
        when(currentNode.getTitle()).thenReturn("Loop Check - Conditional GOTO");
    }

    @Test
    void testConditionMet_UsesBackRouteMatchingTargetTitle() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("conditionKey=continueLoop, conditionValue=true, targetTitle=Content Authoring Team");
        when(workflowDataMap.get("continueLoop", String.class)).thenReturn("true");

        when(workflowSession.getRoutes(workItem, false)).thenReturn(List.of(forwardRoute));

        when(targetNode.getTitle()).thenReturn("Content Authoring Team");
        when(transitionTarget.getTo()).thenReturn(targetNode);
        when(backRouteTarget.getDestinations()).thenReturn(List.of(transitionTarget));
        when(backRouteTarget.getName()).thenReturn("Content Authoring Team");

        when(otherNode.getTitle()).thenReturn("Reviewer Selection");
        when(transitionOther.getTo()).thenReturn(otherNode);
        when(backRouteOther.getDestinations()).thenReturn(List.of(transitionOther));

        when(workflowSession.getBackRoutes(workItem, true))
                .thenReturn(List.of(backRouteOther, backRouteTarget));

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowSession).complete(workItem, backRouteTarget);
        verify(workflowSession, never()).complete(workItem, forwardRoute);
    }

    @Test
    void testConditionMet_FallsBackToFirstBackRoute_WhenTitleNotFound() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("conditionKey=continueLoop, conditionValue=true, targetTitle=Non Existent Step");
        when(workflowDataMap.get("continueLoop", String.class)).thenReturn("true");

        when(workflowSession.getRoutes(workItem, false)).thenReturn(List.of(forwardRoute));

        when(otherNode.getTitle()).thenReturn("Reviewer Selection");
        when(transitionOther.getTo()).thenReturn(otherNode);
        when(backRouteOther.getDestinations()).thenReturn(List.of(transitionOther));
        when(backRouteOther.getName()).thenReturn("Reviewer Selection");

        when(workflowSession.getBackRoutes(workItem, true)).thenReturn(List.of(backRouteOther));

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowSession).complete(workItem, backRouteOther);
        verify(workflowSession, never()).complete(workItem, forwardRoute);
    }

    @Test
    void testConditionMet_EmptyTargetTitle_UsesFirstBackRoute() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("conditionKey=continueLoop, conditionValue=true");
        when(workflowDataMap.get("continueLoop", String.class)).thenReturn("true");

        when(workflowSession.getRoutes(workItem, false)).thenReturn(List.of(forwardRoute));

        when(backRouteOther.getDestinations()).thenReturn(Collections.emptyList());
        when(backRouteOther.getName()).thenReturn("Reviewer Selection");

        when(workflowSession.getBackRoutes(workItem, true)).thenReturn(List.of(backRouteOther));

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowSession).complete(workItem, backRouteOther);
    }

    @Test
    void testConditionMet_NoBackRoutes_FallsThroughToForward() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("conditionKey=continueLoop, conditionValue=true, targetTitle=Content Authoring Team");
        when(workflowDataMap.get("continueLoop", String.class)).thenReturn("true");

        when(workflowSession.getRoutes(workItem, false)).thenReturn(List.of(forwardRoute));
        when(workflowSession.getBackRoutes(workItem, true)).thenReturn(Collections.emptyList());

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowSession).complete(workItem, forwardRoute);
    }

    @Test
    void testConditionNotMet_UsesForwardRoute() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("conditionKey=continueLoop, conditionValue=true, targetTitle=Content Authoring Team");
        when(workflowDataMap.get("continueLoop", String.class)).thenReturn("false");

        when(workflowSession.getRoutes(workItem, false)).thenReturn(List.of(forwardRoute));
        when(workflowSession.getBackRoutes(workItem, true)).thenReturn(List.of(backRouteTarget));

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowSession).complete(workItem, forwardRoute);
        verify(workflowSession, never()).complete(workItem, backRouteTarget);
    }

    @Test
    void testConditionNotMet_NullActualValue_UsesForwardRoute() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("conditionKey=continueLoop, conditionValue=true");
        when(workflowDataMap.get("continueLoop", String.class)).thenReturn(null);

        when(workflowSession.getRoutes(workItem, false)).thenReturn(List.of(forwardRoute));
        when(workflowSession.getBackRoutes(workItem, true)).thenReturn(Collections.emptyList());

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowSession).complete(workItem, forwardRoute);
    }

    @Test
    void testNoRoutesAtAll_DoesNotThrow() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("conditionKey=continueLoop, conditionValue=true");
        when(workflowDataMap.get("continueLoop", String.class)).thenReturn("false");

        when(workflowSession.getRoutes(workItem, false)).thenReturn(Collections.emptyList());
        when(workflowSession.getBackRoutes(workItem, true)).thenReturn(Collections.emptyList());

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowSession, never()).complete(any(), any());
    }

    @Test
    void testEmptyProcessArgs_ConditionNotMet_UsesForwardRoute() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", "")).thenReturn("");
        when(workflowDataMap.get("", String.class)).thenReturn(null);

        when(workflowSession.getRoutes(workItem, false)).thenReturn(List.of(forwardRoute));
        when(workflowSession.getBackRoutes(workItem, true)).thenReturn(Collections.emptyList());

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowSession).complete(workItem, forwardRoute);
    }

    @Test
    void testNullNodeTitle_DoesNotThrow() throws WorkflowException {
        when(workItem.getNode()).thenReturn(null);
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("conditionKey=continueLoop, conditionValue=true");
        when(workflowDataMap.get("continueLoop", String.class)).thenReturn("false");

        when(workflowSession.getRoutes(workItem, false)).thenReturn(List.of(forwardRoute));
        when(workflowSession.getBackRoutes(workItem, true)).thenReturn(Collections.emptyList());

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowSession).complete(workItem, forwardRoute);
    }

    // ========================================================================
    // Per-gate Loop Check scenario tests
    // With the new workflow design, each review gate has its own Loop Check
    // step immediately after it. These tests verify the per-gate behavior.
    // ========================================================================

    @Test
    void testPerGate_FeedbackAtFirstGate_ImmediateLoopBack() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("conditionKey=continueLoop, conditionValue=true, targetTitle=Content Authoring Team");
        when(workflowDataMap.get("continueLoop", String.class)).thenReturn("true");

        when(workflowSession.getRoutes(workItem, false)).thenReturn(List.of(forwardRoute));

        when(targetNode.getTitle()).thenReturn("Content Authoring Team");
        when(transitionTarget.getTo()).thenReturn(targetNode);
        when(backRouteTarget.getDestinations()).thenReturn(List.of(transitionTarget));
        when(backRouteTarget.getName()).thenReturn("Content Authoring Team");
        when(workflowSession.getBackRoutes(workItem, true)).thenReturn(List.of(backRouteTarget));

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowSession).complete(workItem, backRouteTarget);
        verify(workflowSession, never()).complete(workItem, forwardRoute);
    }

    @Test
    void testPerGate_ApprovedAtGate_AdvancesToNextGate() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("conditionKey=continueLoop, conditionValue=true, targetTitle=Content Authoring Team");
        when(workflowDataMap.get("continueLoop", String.class)).thenReturn("false");

        when(workflowSession.getRoutes(workItem, false)).thenReturn(List.of(forwardRoute));
        when(workflowSession.getBackRoutes(workItem, true)).thenReturn(List.of(backRouteTarget));

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowSession).complete(workItem, forwardRoute);
        verify(workflowSession, never()).complete(workItem, backRouteTarget);
    }

    @Test
    void testPerGate_GateSkipped_ContinuesToNextGate() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("conditionKey=continueLoop, conditionValue=true, targetTitle=Content Authoring Team");
        when(workflowDataMap.get("continueLoop", String.class)).thenReturn("false");

        when(workflowSession.getRoutes(workItem, false)).thenReturn(List.of(forwardRoute));
        when(workflowSession.getBackRoutes(workItem, true)).thenReturn(Collections.emptyList());

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowSession).complete(workItem, forwardRoute);
    }

    @Test
    void testPerGate_ResetFlagBeforeGates_AllGatesAdvanceForward() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("conditionKey=continueLoop, conditionValue=true, targetTitle=Content Authoring Team");
        when(workflowDataMap.get("continueLoop", String.class)).thenReturn("false");

        when(workflowSession.getRoutes(workItem, false)).thenReturn(List.of(forwardRoute));
        when(workflowSession.getBackRoutes(workItem, true)).thenReturn(List.of(backRouteTarget));

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowSession).complete(workItem, forwardRoute);
        verify(workflowSession, never()).complete(workItem, backRouteTarget);
    }

    @Test
    void testPerGate_FeedbackAtLastGate_LoopsBack() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("conditionKey=continueLoop, conditionValue=true, targetTitle=Content Authoring Team");
        when(workflowDataMap.get("continueLoop", String.class)).thenReturn("true");

        when(workflowSession.getRoutes(workItem, false)).thenReturn(List.of(forwardRoute));

        when(targetNode.getTitle()).thenReturn("Content Authoring Team");
        when(transitionTarget.getTo()).thenReturn(targetNode);
        when(backRouteTarget.getDestinations()).thenReturn(List.of(transitionTarget));
        when(backRouteTarget.getName()).thenReturn("Content Authoring Team");

        when(otherNode.getTitle()).thenReturn("Reviewer Selection");
        when(transitionOther.getTo()).thenReturn(otherNode);
        when(backRouteOther.getDestinations()).thenReturn(List.of(transitionOther));

        when(workflowSession.getBackRoutes(workItem, true))
                .thenReturn(List.of(backRouteOther, backRouteTarget));

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowSession).complete(workItem, backRouteTarget);
        verify(workflowSession, never()).complete(workItem, forwardRoute);
    }

    @Test
    void testPerGate_MultipleBackRoutes_SelectsCorrectTarget() throws WorkflowException {
        when(processArgs.get("PROCESS_ARGS", ""))
                .thenReturn("conditionKey=continueLoop, conditionValue=true, targetTitle=Content Authoring Team");
        when(workflowDataMap.get("continueLoop", String.class)).thenReturn("true");

        Route backRouteReviewerSelection = mock(Route.class);
        WorkflowTransition transReviewerSelection = mock(WorkflowTransition.class);
        WorkflowNode nodeReviewerSelection = mock(WorkflowNode.class);
        when(nodeReviewerSelection.getTitle()).thenReturn("Reviewer Selection");
        when(transReviewerSelection.getTo()).thenReturn(nodeReviewerSelection);
        when(backRouteReviewerSelection.getDestinations()).thenReturn(List.of(transReviewerSelection));

        Route backRouteResetLoop = mock(Route.class);
        WorkflowTransition transResetLoop = mock(WorkflowTransition.class);
        WorkflowNode nodeResetLoop = mock(WorkflowNode.class);
        when(nodeResetLoop.getTitle()).thenReturn("Reset Loop Flag");
        when(transResetLoop.getTo()).thenReturn(nodeResetLoop);
        when(backRouteResetLoop.getDestinations()).thenReturn(List.of(transResetLoop));

        when(targetNode.getTitle()).thenReturn("Content Authoring Team");
        when(transitionTarget.getTo()).thenReturn(targetNode);
        when(backRouteTarget.getDestinations()).thenReturn(List.of(transitionTarget));
        when(backRouteTarget.getName()).thenReturn("Content Authoring Team");

        when(workflowSession.getRoutes(workItem, false)).thenReturn(List.of(forwardRoute));
        when(workflowSession.getBackRoutes(workItem, true))
                .thenReturn(List.of(backRouteReviewerSelection, backRouteResetLoop, backRouteTarget));

        process.execute(workItem, workflowSession, processArgs);

        verify(workflowSession).complete(workItem, backRouteTarget);
        verify(workflowSession, never()).complete(workItem, forwardRoute);
        verify(workflowSession, never()).complete(workItem, backRouteReviewerSelection);
        verify(workflowSession, never()).complete(workItem, backRouteResetLoop);
    }
}
