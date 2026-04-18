package com.mvw.core.workflow;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;

@ExtendWith(MockitoExtension.class)
class ReplicateContentProcessTest {

    @InjectMocks
    private ReplicateContentProcess process;

    @Mock
    private Replicator replicator;

    @Mock
    private ResourceResolverFactory resolverFactory;

    @Mock
    private WorkItem workItem;

    @Mock
    private WorkflowSession workflowSession;

    @Mock
    private MetaDataMap metaDataMap;

    @Mock
    private WorkflowData workflowData;

    @Mock
    private ResourceResolver serviceResolver;

    @Mock
    private Session session;

    @BeforeEach
    void setUp() {
        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn("/content/test/page");
    }

    @Test
    void testExecute_Success() throws Exception {
        when(metaDataMap.get("PROCESS_ARGS", "publish")).thenReturn("publish");
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(Session.class)).thenReturn(session);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(replicator).replicate(eq(session), eq(ReplicationActionType.ACTIVATE), eq("/content/test/page"), any(ReplicationOptions.class));
    }

    @Test
    void testExecute_PreviewAgent() throws Exception {
        when(metaDataMap.get("PROCESS_ARGS", "publish")).thenReturn("preview");
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(Session.class)).thenReturn(session);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(replicator).replicate(eq(session), eq(ReplicationActionType.ACTIVATE), eq("/content/test/page"), any(ReplicationOptions.class));
    }

    @Test
    void testExecute_LoginException() throws Exception {
        when(metaDataMap.get("PROCESS_ARGS", "publish")).thenReturn("publish");
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenThrow(new LoginException("Login failed"));

        assertThrows(WorkflowException.class, () -> {
            process.execute(workItem, workflowSession, metaDataMap);
        });
    }

    @Test
    void testExecute_NullSession() throws Exception {
        when(metaDataMap.get("PROCESS_ARGS", "publish")).thenReturn("publish");
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(Session.class)).thenReturn(null);

        assertThrows(WorkflowException.class, () -> {
            process.execute(workItem, workflowSession, metaDataMap);
        });
    }

    @Test
    void testExecute_ReplicationException() throws Exception {
        when(metaDataMap.get("PROCESS_ARGS", "publish")).thenReturn("publish");
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(Session.class)).thenReturn(session);
        doThrow(new ReplicationException("Replication failed"))
            .when(replicator).replicate(any(Session.class), any(ReplicationActionType.class), anyString(), any(ReplicationOptions.class));

        assertThrows(WorkflowException.class, () -> {
            process.execute(workItem, workflowSession, metaDataMap);
        });
    }

    @Test
    void testExecute_DefaultAgentId() throws Exception {
        when(metaDataMap.get("PROCESS_ARGS", "publish")).thenReturn("publish");
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(Session.class)).thenReturn(session);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(replicator).replicate(eq(session), eq(ReplicationActionType.ACTIVATE), eq("/content/test/page"), any(ReplicationOptions.class));
    }

    @Test
    void testExecute_WhitespaceInAgentId() throws Exception {
        when(metaDataMap.get("PROCESS_ARGS", "publish")).thenReturn("  publish  ");
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(Session.class)).thenReturn(session);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(replicator).replicate(eq(session), eq(ReplicationActionType.ACTIVATE), eq("/content/test/page"), any(ReplicationOptions.class));
    }
}
