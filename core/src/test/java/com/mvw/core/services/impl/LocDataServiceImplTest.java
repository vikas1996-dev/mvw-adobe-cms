package com.mvw.core.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import javax.jcr.Session;
import javax.servlet.http.Cookie;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

@ExtendWith(MockitoExtension.class)
class LocDataServiceImplTest {

    private LocDataServiceImpl service;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private SlingHttpServletResponse response;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private Query query;

    @Mock
    private SearchResult searchResult;

    @Mock
    private Hit hit;

    @Mock
    private Resource cfResource;

    @Mock
    private ContentFragment contentFragment;

    @Mock
    private ContentElement contentElement;

    @Mock
    private Session session;

    @BeforeEach
    void setUp() {
        service = new LocDataServiceImpl();
        when(request.getResourceResolver()).thenReturn(resourceResolver);
    }

    @Test
    void testProcessLocData_NoLocParam_DefaultLoc() {
        when(request.getParameter("loc")).thenReturn(null);
        when(resourceResolver.getResource(anyString())).thenReturn(cfResource);
        when(cfResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElement("locCode")).thenReturn(contentElement);
        when(contentFragment.getElement("phoneNumber")).thenReturn(contentElement);
        when(contentElement.getContent()).thenReturn("DEFAULT");

        Map<String, String> result = service.processLocData(request, response);

        assertNotNull(result);
        verify(request).setAttribute("loc", "DEFAULT");
    }

    @Test
    void testProcessLocData_WithLocParam_NotFound() {
        when(request.getParameter("loc")).thenReturn("US");
        when(resourceResolver.adaptTo(QueryBuilder.class)).thenReturn(queryBuilder);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(queryBuilder.createQuery(any(), eq(session))).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        when(searchResult.getHits()).thenReturn(java.util.Collections.emptyList());
        
        // For default loc
        when(resourceResolver.getResource(anyString())).thenReturn(cfResource);
        when(cfResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElement("locCode")).thenReturn(contentElement);
        when(contentFragment.getElement("phoneNumber")).thenReturn(contentElement);
        when(contentElement.getContent()).thenReturn("DEFAULT");
        when(request.getCookies()).thenReturn(null);

        Map<String, String> result = service.processLocData(request, response);

        assertNotNull(result);
    }

    @Test
    void testProcessLocData_WithLocParam_Found() throws Exception {
        when(request.getParameter("loc")).thenReturn("US");
        when(resourceResolver.adaptTo(QueryBuilder.class)).thenReturn(queryBuilder);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(queryBuilder.createQuery(any(), eq(session))).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        when(searchResult.getHits()).thenReturn(java.util.Collections.singletonList(hit));
        when(hit.getResource()).thenReturn(cfResource);
        when(cfResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElement("phoneNumber")).thenReturn(contentElement);
        when(contentElement.getContent()).thenReturn("1-800-555-1234");
        when(request.getCookies()).thenReturn(null);

        Map<String, String> result = service.processLocData(request, response);

        assertNotNull(result);
        assertEquals("US", result.get("loc"));
        assertEquals("1-800-555-1234", result.get("phoneNumber"));
        verify(response).addCookie(any(Cookie.class));
    }

    @Test
    void testProcessLocData_WithExistingCookie() throws Exception {
        when(request.getParameter("loc")).thenReturn("CA");
        when(resourceResolver.adaptTo(QueryBuilder.class)).thenReturn(queryBuilder);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(queryBuilder.createQuery(any(), eq(session))).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        when(searchResult.getHits()).thenReturn(java.util.Collections.singletonList(hit));
        when(hit.getResource()).thenReturn(cfResource);
        when(cfResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElement("phoneNumber")).thenReturn(contentElement);
        when(contentElement.getContent()).thenReturn("1-800-555-5678");
        
        Cookie existingCookie = new Cookie("loc", "OLD");
        when(request.getCookies()).thenReturn(new Cookie[]{existingCookie});

        Map<String, String> result = service.processLocData(request, response);

        assertNotNull(result);
        verify(response).addCookie(any(Cookie.class));
    }

    @Test
    void testProcessLocData_WithNonMatchingCookie() throws Exception {
        when(request.getParameter("loc")).thenReturn("UK");
        when(resourceResolver.adaptTo(QueryBuilder.class)).thenReturn(queryBuilder);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(queryBuilder.createQuery(any(), eq(session))).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        when(searchResult.getHits()).thenReturn(java.util.Collections.singletonList(hit));
        when(hit.getResource()).thenReturn(cfResource);
        when(cfResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElement("phoneNumber")).thenReturn(contentElement);
        when(contentElement.getContent()).thenReturn("44-800-555-5678");
        
        Cookie otherCookie = new Cookie("other", "value");
        when(request.getCookies()).thenReturn(new Cookie[]{otherCookie});

        Map<String, String> result = service.processLocData(request, response);

        assertNotNull(result);
        verify(response).addCookie(any(Cookie.class));
    }

    @Test
    void testProcessLocData_NullContentFragment() {
        when(request.getParameter("loc")).thenReturn(null);
        when(resourceResolver.getResource(anyString())).thenReturn(cfResource);
        when(cfResource.adaptTo(ContentFragment.class)).thenReturn(null);

        Map<String, String> result = service.processLocData(request, response);

        assertNotNull(result);
    }

    @Test
    void testProcessLocData_NullResource() {
        when(request.getParameter("loc")).thenReturn(null);
        when(resourceResolver.getResource(anyString())).thenReturn(null);

        Map<String, String> result = service.processLocData(request, response);

        assertNotNull(result);
    }

    @Test
    void testProcessLocData_NullElement() {
        when(request.getParameter("loc")).thenReturn(null);
        when(resourceResolver.getResource(anyString())).thenReturn(cfResource);
        when(cfResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElement("locCode")).thenReturn(null);

        Map<String, String> result = service.processLocData(request, response);

        assertNotNull(result);
    }

    @Test
    void testProcessLocData_NullElementContent() {
        when(request.getParameter("loc")).thenReturn(null);
        when(resourceResolver.getResource(anyString())).thenReturn(cfResource);
        when(cfResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElement("locCode")).thenReturn(contentElement);
        when(contentElement.getContent()).thenReturn(null);

        Map<String, String> result = service.processLocData(request, response);

        assertNotNull(result);
    }

    @Test
    void testProcessLocData_ExceptionInQuery() throws Exception {
        when(request.getParameter("loc")).thenReturn("US");
        when(resourceResolver.adaptTo(QueryBuilder.class)).thenReturn(queryBuilder);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(queryBuilder.createQuery(any(), eq(session))).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        when(searchResult.getHits()).thenReturn(java.util.Collections.singletonList(hit));
        when(hit.getResource()).thenThrow(new RuntimeException("Error"));

        Map<String, String> result = service.processLocData(request, response);

        assertNotNull(result);
    }

    @Test
    void testProcessLocData_EmptyLocParam() {
        when(request.getParameter("loc")).thenReturn("");
        when(resourceResolver.getResource(anyString())).thenReturn(cfResource);
        when(cfResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElement("locCode")).thenReturn(contentElement);
        when(contentFragment.getElement("phoneNumber")).thenReturn(contentElement);
        when(contentElement.getContent()).thenReturn("DEFAULT");

        Map<String, String> result = service.processLocData(request, response);

        assertNotNull(result);
    }

    @Test
    void testProcessLocData_NullLocCode_NullResponse() {
        when(request.getParameter("loc")).thenReturn(null);
        when(resourceResolver.getResource(anyString())).thenReturn(cfResource);
        when(cfResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElement("locCode")).thenReturn(contentElement);
        when(contentFragment.getElement("phoneNumber")).thenReturn(contentElement);
        // Return null for locCode content
        when(contentElement.getContent()).thenReturn(null);

        // Use null response to cover the null response branch
        Map<String, String> result = service.processLocData(request, null);

        assertNotNull(result);
    }

    @Test
    void testProcessLocData_HandleDefaultLoc_NullLocCode() {
        when(request.getParameter("loc")).thenReturn(null);
        when(resourceResolver.getResource(anyString())).thenReturn(cfResource);
        when(cfResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        // Return null for locCode element
        when(contentFragment.getElement("locCode")).thenReturn(null);

        Map<String, String> result = service.processLocData(request, response);

        assertNotNull(result);
    }

    @Test
    void testProcessLocData_HandleDefaultLoc_WithNullResponse() {
        when(request.getParameter("loc")).thenReturn(null);
        when(resourceResolver.getResource(anyString())).thenReturn(cfResource);
        when(cfResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElement("locCode")).thenReturn(contentElement);
        when(contentFragment.getElement("phoneNumber")).thenReturn(contentElement);
        when(contentElement.getContent()).thenReturn("DEFAULT");

        // Pass null response to cover the branch where response is null
        Map<String, String> result = service.processLocData(request, null);

        assertNotNull(result);
        // Cookie should not be added when response is null
    }

    @Test
    void testProcessLocData_WithLocParam_FoundCF_WithExistingMatchingCookie() throws Exception {
        when(request.getParameter("loc")).thenReturn("US");
        when(resourceResolver.adaptTo(QueryBuilder.class)).thenReturn(queryBuilder);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(queryBuilder.createQuery(any(), eq(session))).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        when(searchResult.getHits()).thenReturn(java.util.Collections.singletonList(hit));
        when(hit.getResource()).thenReturn(cfResource);
        when(cfResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElement("phoneNumber")).thenReturn(contentElement);
        when(contentElement.getContent()).thenReturn("1-800-555-1234");
        
        // Cookie that matches the loc cookie name
        Cookie locCookie = new Cookie("loc", "OLD_VALUE");
        when(request.getCookies()).thenReturn(new Cookie[]{locCookie});

        Map<String, String> result = service.processLocData(request, response);

        assertNotNull(result);
        assertEquals("US", result.get("loc"));
        assertEquals("1-800-555-1234", result.get("phoneNumber"));
        verify(response).addCookie(any(Cookie.class));
    }

    @Test
    void testProcessLocData_GetElementValue_AllBranches() {
        // Test null resource
        when(request.getParameter("loc")).thenReturn(null);
        when(resourceResolver.getResource(anyString())).thenReturn(null);

        Map<String, String> result = service.processLocData(request, response);

        assertNotNull(result);
        // Should return element name when resource is null
    }

    @Test
    void testProcessLocData_GetElementValue_NullContentFragment() {
        when(request.getParameter("loc")).thenReturn(null);
        when(resourceResolver.getResource(anyString())).thenReturn(cfResource);
        when(cfResource.adaptTo(ContentFragment.class)).thenReturn(null);

        Map<String, String> result = service.processLocData(request, response);

        assertNotNull(result);
    }

    @Test
    void testProcessLocData_GetElementValue_NullElement() {
        when(request.getParameter("loc")).thenReturn(null);
        when(resourceResolver.getResource(anyString())).thenReturn(cfResource);
        when(cfResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElement("locCode")).thenReturn(null);

        Map<String, String> result = service.processLocData(request, response);

        assertNotNull(result);
    }

    @Test
    void testProcessLocData_GetElementValue_NullElementContent() {
        when(request.getParameter("loc")).thenReturn(null);
        when(resourceResolver.getResource(anyString())).thenReturn(cfResource);
        when(cfResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElement("locCode")).thenReturn(contentElement);
        when(contentFragment.getElement("phoneNumber")).thenReturn(contentElement);
        when(contentElement.getContent()).thenReturn(null);

        Map<String, String> result = service.processLocData(request, response);

        assertNotNull(result);
    }

    @Test
    void testProcessLocData_MultipleCookiesNoneMatching() throws Exception {
        when(request.getParameter("loc")).thenReturn("FR");
        when(resourceResolver.adaptTo(QueryBuilder.class)).thenReturn(queryBuilder);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(queryBuilder.createQuery(any(), eq(session))).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        when(searchResult.getHits()).thenReturn(java.util.Collections.singletonList(hit));
        when(hit.getResource()).thenReturn(cfResource);
        when(cfResource.adaptTo(ContentFragment.class)).thenReturn(contentFragment);
        when(contentFragment.getElement("phoneNumber")).thenReturn(contentElement);
        when(contentElement.getContent()).thenReturn("33-800-555-5678");
        
        // Multiple cookies but none matching "loc"
        Cookie cookie1 = new Cookie("session", "abc123");
        Cookie cookie2 = new Cookie("user", "john");
        Cookie cookie3 = new Cookie("pref", "dark");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie1, cookie2, cookie3});

        Map<String, String> result = service.processLocData(request, response);

        assertNotNull(result);
        verify(response).addCookie(any(Cookie.class));
    }
}
