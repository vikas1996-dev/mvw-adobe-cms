package com.mvw.core.listeners;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ManagePublicationAbsoluteTimeListenerTest {

    @InjectMocks
    private ManagePublicationAbsoluteTimeListener listener;

    @Mock
    private ResourceResolverFactory resolverFactory;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Session session;

    @Mock
    private Resource instanceResource;

    @Mock
    private Resource dataResource;

    @Mock
    private Resource metaResource;

    @Mock
    private Resource payloadResource;

    @Mock
    private Resource damMetaResource;

    @Mock
    private Resource payloadPathResource;

    @Mock
    private Resource payloadParentResource;

    @Mock
    private Resource payloadParentContentResource;

    @Mock
    private ValueMap instanceValueMap;

    @Mock
    private ValueMap metaValueMap;

    @Mock
    private ValueMap payloadValueMap;

    @Mock
    private ValueMap damMetaValueMap;

    @Mock
    private ModifiableValueMap modifiableDamValueMap;

    @Mock
    private ModifiableValueMap modifiableMetaValueMap;

    @BeforeEach
    void setUp() throws Exception {
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(session.getUserID()).thenReturn("service-user");
    }

    @Test
    void testOnChange_LoginException() throws Exception {
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenThrow(new LoginException("Login failed"));

        List<ResourceChange> changes = new ArrayList<>();
        changes.add(createMockChange("/var/workflow/instances/test/data"));

        listener.onChange(changes);

        verify(resourceResolver, never()).getResource(anyString());
    }

    @Test
    void testOnChange_RepositoryException() throws Exception {
        when(session.hasPermission(anyString(), anyString())).thenThrow(new RepositoryException("Error"));

        List<ResourceChange> changes = new ArrayList<>();
        changes.add(createMockChange("/var/workflow/instances/test/data"));

        listener.onChange(changes);
    }

    @Test
    void testOnChange_NullInstanceResource() throws Exception {
        when(session.hasPermission(anyString(), anyString())).thenReturn(true);
        when(resourceResolver.getResource("/var/workflow/instances/test")).thenReturn(null);

        List<ResourceChange> changes = new ArrayList<>();
        changes.add(createMockChange("/var/workflow/instances/test/data"));

        listener.onChange(changes);
    }

    @Test
    void testOnChange_WrongModelId() throws Exception {
        when(session.hasPermission(anyString(), anyString())).thenReturn(true);
        when(resourceResolver.getResource("/var/workflow/instances/test")).thenReturn(instanceResource);
        when(instanceResource.getValueMap()).thenReturn(instanceValueMap);
        when(instanceValueMap.get("modelId", String.class)).thenReturn("/var/workflow/models/other_model");

        List<ResourceChange> changes = new ArrayList<>();
        changes.add(createMockChange("/var/workflow/instances/test/data"));

        listener.onChange(changes);
    }

    @Test
    void testOnChange_NullModelId() throws Exception {
        when(session.hasPermission(anyString(), anyString())).thenReturn(true);
        when(resourceResolver.getResource("/var/workflow/instances/test")).thenReturn(instanceResource);
        when(instanceResource.getValueMap()).thenReturn(instanceValueMap);
        when(instanceValueMap.get("modelId", String.class)).thenReturn(null);

        List<ResourceChange> changes = new ArrayList<>();
        changes.add(createMockChange("/var/workflow/instances/test/data"));

        listener.onChange(changes);
    }

    @Test
    void testOnChange_NonDamPayload() throws Exception {
        when(session.hasPermission(anyString(), anyString())).thenReturn(true);
        when(resourceResolver.getResource("/var/workflow/instances/test")).thenReturn(instanceResource);
        when(instanceResource.getValueMap()).thenReturn(instanceValueMap);
        when(instanceResource.getPath()).thenReturn("/var/workflow/instances/test");
        when(instanceValueMap.get("modelId", String.class)).thenReturn("/var/workflow/models/scheduled_tree_activation");
        when(instanceValueMap.get("status", String.class)).thenReturn("RUNNING");
        
        when(resourceResolver.getResource("/var/workflow/instances/test/data")).thenReturn(dataResource);
        when(resourceResolver.getResource("/var/workflow/instances/test/data/metaData")).thenReturn(metaResource);
        when(metaResource.getValueMap()).thenReturn(metaValueMap);
        
        when(dataResource.getChild("payload")).thenReturn(payloadResource);
        when(payloadResource.getValueMap()).thenReturn(payloadValueMap);
        when(payloadValueMap.get("path", String.class)).thenReturn("/content/site/page");

        List<ResourceChange> changes = new ArrayList<>();
        changes.add(createMockChange("/var/workflow/instances/test/data"));

        listener.onChange(changes);

        verify(resourceResolver, never()).commit();
    }

    @Test
    void testOnChange_NullAbsoluteTime() throws Exception {
        when(session.hasPermission(anyString(), anyString())).thenReturn(true);
        when(resourceResolver.getResource("/var/workflow/instances/test")).thenReturn(instanceResource);
        when(instanceResource.getValueMap()).thenReturn(instanceValueMap);
        when(instanceResource.getPath()).thenReturn("/var/workflow/instances/test");
        when(instanceValueMap.get("modelId", String.class)).thenReturn("/var/workflow/models/scheduled_tree_activation");
        when(instanceValueMap.get("status", String.class)).thenReturn("RUNNING");
        
        when(resourceResolver.getResource("/var/workflow/instances/test/data")).thenReturn(dataResource);
        when(resourceResolver.getResource("/var/workflow/instances/test/data/metaData")).thenReturn(metaResource);
        when(metaResource.getValueMap()).thenReturn(metaValueMap);
        
        when(dataResource.getChild("payload")).thenReturn(payloadResource);
        when(payloadResource.getValueMap()).thenReturn(payloadValueMap);
        when(payloadValueMap.get("path", String.class)).thenReturn("/content/dam/legal/test/asset.pdf");
        
        when(resourceResolver.getResource("/content/dam/legal/test/asset.pdf/jcr:content/metadata")).thenReturn(damMetaResource);
        when(resourceResolver.getResource("/content/dam/legal/test/asset.pdf")).thenReturn(payloadPathResource);
        when(damMetaResource.getValueMap()).thenReturn(damMetaValueMap);
        
        when(metaValueMap.get("absoluteTime")).thenReturn(null);

        List<ResourceChange> changes = new ArrayList<>();
        changes.add(createMockChange("/var/workflow/instances/test/data"));

        listener.onChange(changes);

        verify(resourceResolver, never()).commit();
    }

    @Test
    void testOnChange_NonRunningWorkflowStatus() throws Exception {
        when(session.hasPermission(anyString(), anyString())).thenReturn(true);
        when(resourceResolver.getResource("/var/workflow/instances/test")).thenReturn(instanceResource);
        when(instanceResource.getValueMap()).thenReturn(instanceValueMap);
        when(instanceResource.getPath()).thenReturn("/var/workflow/instances/test");
        when(instanceValueMap.get("modelId", String.class)).thenReturn("/var/workflow/models/scheduled_tree_activation");
        when(instanceValueMap.get("status", String.class)).thenReturn("ABORTED");

        List<ResourceChange> changes = new ArrayList<>();
        changes.add(createMockChange("/var/workflow/instances/test/data"));

        listener.onChange(changes);

        verify(resourceResolver, never()).getResource("/var/workflow/instances/test/data");
        verify(resourceResolver, never()).commit();
    }

    private ResourceChange createMockChange(String path) {
        ResourceChange change = mock(ResourceChange.class);
        when(change.getPath()).thenReturn(path);
        return change;
    }
}
