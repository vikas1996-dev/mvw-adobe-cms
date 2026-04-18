package com.mvw.core.servlets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Iterator;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.adobe.granite.ui.components.ds.DataSource;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReviewersDataSourceServletTest {

    @InjectMocks
    private ReviewersDataSourceServlet servlet;

    @Mock
    private ResourceResolverFactory resolverFactory;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private SlingHttpServletResponse response;

    @Mock
    private ResourceResolver serviceResolver;

    @Mock
    private ResourceResolver requestResolver;

    @Mock
    private UserManager userManager;

    @Mock
    private Group group;

    @Mock
    private User user;

    @Mock
    private Resource requestResource;

    @Mock
    private Resource datasourceResource;

    @Mock
    private ValueMap valueMap;

    @BeforeEach
    void setUp() throws Exception {
        when(request.getResourceResolver()).thenReturn(requestResolver);
        when(request.getResource()).thenReturn(requestResource);
    }

    @Test
    void testDoGet_Success() throws Exception {
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        
        when(requestResource.getChild("datasource")).thenReturn(datasourceResource);
        when(datasourceResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("targetGroup", "")).thenReturn("testGroup");
        
        when(userManager.getAuthorizable("testGroup")).thenReturn(group);
        when(group.getMembers()).thenReturn(Collections.emptyIterator());

        servlet.doGet(request, response);

        verify(request).setAttribute(eq(DataSource.class.getName()), any(DataSource.class));
    }

    @Test
    void testDoGet_WithMembers() throws Exception {
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        
        when(requestResource.getChild("datasource")).thenReturn(datasourceResource);
        when(datasourceResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("targetGroup", "")).thenReturn("testGroup");
        
        when(userManager.getAuthorizable("testGroup")).thenReturn(group);
        
        Iterator<Authorizable> memberIterator = Collections.singletonList((Authorizable) user).iterator();
        when(group.getMembers()).thenReturn(memberIterator);
        when(user.isGroup()).thenReturn(false);
        when(user.getID()).thenReturn("testUser");

        servlet.doGet(request, response);

        verify(request).setAttribute(eq(DataSource.class.getName()), any(DataSource.class));
    }

    @Test
    void testDoGet_LoginException() throws Exception {
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenThrow(new LoginException("Login failed"));

        servlet.doGet(request, response);

        verify(request).setAttribute(eq(DataSource.class.getName()), any(DataSource.class));
    }

    @Test
    void testDoGet_NullUserManager() throws Exception {
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(UserManager.class)).thenReturn(null);
        
        when(requestResource.getChild("datasource")).thenReturn(datasourceResource);
        when(datasourceResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("targetGroup", "")).thenReturn("testGroup");

        servlet.doGet(request, response);

        verify(request).setAttribute(eq(DataSource.class.getName()), any(DataSource.class));
    }

    @Test
    void testDoGet_NullTargetGroup() throws Exception {
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        
        when(requestResource.getChild("datasource")).thenReturn(null);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq(DataSource.class.getName()), any(DataSource.class));
    }

    @Test
    void testDoGet_AuthorizableNotGroup() throws Exception {
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        
        when(requestResource.getChild("datasource")).thenReturn(datasourceResource);
        when(datasourceResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("targetGroup", "")).thenReturn("testUser");
        
        when(userManager.getAuthorizable("testUser")).thenReturn(user);
        when(user.isGroup()).thenReturn(false);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq(DataSource.class.getName()), any(DataSource.class));
    }

    @Test
    void testDoGet_AuthorizableNull() throws Exception {
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        
        when(requestResource.getChild("datasource")).thenReturn(datasourceResource);
        when(datasourceResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("targetGroup", "")).thenReturn("nonexistent");
        
        when(userManager.getAuthorizable("nonexistent")).thenReturn(null);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq(DataSource.class.getName()), any(DataSource.class));
    }

    @Test
    void testDoGet_MemberIsGroup() throws Exception {
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        
        when(requestResource.getChild("datasource")).thenReturn(datasourceResource);
        when(datasourceResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("targetGroup", "")).thenReturn("testGroup");
        
        when(userManager.getAuthorizable("testGroup")).thenReturn(group);
        
        Group subGroup = mock(Group.class);
        Iterator<Authorizable> memberIterator = Collections.singletonList((Authorizable) subGroup).iterator();
        when(group.getMembers()).thenReturn(memberIterator);
        when(subGroup.isGroup()).thenReturn(true);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq(DataSource.class.getName()), any(DataSource.class));
    }

    @Test
    void testDoGet_GeneralException() throws Exception {
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        
        when(requestResource.getChild("datasource")).thenReturn(datasourceResource);
        when(datasourceResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("targetGroup", "")).thenReturn("testGroup");
        
        when(userManager.getAuthorizable("testGroup")).thenThrow(new RepositoryException("Error"));

        servlet.doGet(request, response);

        verify(request).setAttribute(eq(DataSource.class.getName()), any(DataSource.class));
    }
}
