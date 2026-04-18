package com.mvw.core.servlets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@ExtendWith(MockitoExtension.class)
class ResortPageCreationServletTest {

    @InjectMocks
    private ResortPageCreationServlet servlet;

    @Mock
    private SlingSettingsService slingSettingsService;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private SlingHttpServletResponse response;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private PageManager pageManager;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private Page page;

    @Mock
    private Resource contentResource;

    @Mock
    private ModifiableValueMap modifiableValueMap;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testDoGet_NotAuthorInstance() throws Exception {
        Set<String> runModes = new HashSet<>();
        runModes.add("publish");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("only available on Author"));
    }

    @Test
    void testDoGet_NullPageManager() throws Exception {
        Set<String> runModes = new HashSet<>();
        runModes.add("author");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(null);
        when(request.getParameter("parentPath")).thenReturn(null);
        when(request.getParameter("template")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("PageManager is null"));
    }

    @Test
    void testDoGet_NullRequestDispatcher() throws Exception {
        Set<String> runModes = new HashSet<>();
        runModes.add("author");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(request.getParameter("parentPath")).thenReturn(null);
        when(request.getParameter("template")).thenReturn(null);
        when(request.getRequestDispatcher("/bin/mvw/resort-list")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    void testDoGet_WithCustomPaths() throws Exception {
        Set<String> runModes = new HashSet<>();
        runModes.add("author");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(request.getParameter("parentPath")).thenReturn("/content/custom/path");
        when(request.getParameter("template")).thenReturn("/conf/custom/template");
        when(request.getRequestDispatcher("/bin/mvw/resort-list")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    void testDoGet_NoResortsInResponse() throws Exception {
        Set<String> runModes = new HashSet<>();
        runModes.add("author");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(request.getParameter("parentPath")).thenReturn(null);
        when(request.getParameter("template")).thenReturn(null);
        when(request.getRequestDispatcher("/bin/mvw/resort-list")).thenReturn(requestDispatcher);
        
        // Mock response wrapper behavior - return JSON without "resorts" key
        // The servlet creates a CharResponseWrapper internally, so we need to write to that wrapper's writer
        doAnswer(invocation -> {
            HttpServletResponse respWrapper = invocation.getArgument(1);
            PrintWriter writer = respWrapper.getWriter();
            writer.write("{\"data\":[]}");
            writer.flush();
            return null;
        }).when(requestDispatcher).include(eq(request), any(HttpServletResponse.class));

        servlet.doGet(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("No resorts found"), "Expected 'No resorts found' in output but got: " + output);
    }

    @Test
    void testDoGet_SuccessfulPageCreation() throws Exception {
        Set<String> runModes = new HashSet<>();
        runModes.add("author");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(request.getParameter("parentPath")).thenReturn(null);
        when(request.getParameter("template")).thenReturn(null);
        when(request.getRequestDispatcher("/bin/mvw/resort-list")).thenReturn(requestDispatcher);
        
        // Mock JSON response with resorts
        String resortsJson = "{\"resorts\":[{\"slug\":\"test-resort\",\"marshaCode\":\"ABC\",\"universalPropertyCode\":\"UPC123\"}]}";
        doAnswer(invocation -> {
            HttpServletResponse respWrapper = invocation.getArgument(1);
            PrintWriter writer = respWrapper.getWriter();
            writer.write(resortsJson);
            writer.flush();
            return null;
        }).when(requestDispatcher).include(eq(request), any(HttpServletResponse.class));

        // Page doesn't exist, so it will be created
        when(pageManager.getPage("/content/tmvcs/us/en/experiences/resorts/test-resort")).thenReturn(null);
        when(pageManager.create(anyString(), eq("test-resort"), anyString(), anyString())).thenReturn(page);
        when(page.getPath()).thenReturn("/content/tmvcs/us/en/experiences/resorts/test-resort");
        when(page.getContentResource()).thenReturn(contentResource);
        when(contentResource.adaptTo(ModifiableValueMap.class)).thenReturn(modifiableValueMap);

        servlet.doGet(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("Created page"));
        verify(resourceResolver).commit();
    }

    @Test
    void testDoGet_PageAlreadyExists() throws Exception {
        Set<String> runModes = new HashSet<>();
        runModes.add("author");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(request.getParameter("parentPath")).thenReturn(null);
        when(request.getParameter("template")).thenReturn(null);
        when(request.getRequestDispatcher("/bin/mvw/resort-list")).thenReturn(requestDispatcher);
        
        String resortsJson = "{\"resorts\":[{\"slug\":\"existing-resort\",\"marshaCode\":\"DEF\",\"universalPropertyCode\":\"UPC456\"}]}";
        doAnswer(invocation -> {
            HttpServletResponse respWrapper = invocation.getArgument(1);
            PrintWriter writer = respWrapper.getWriter();
            writer.write(resortsJson);
            writer.flush();
            return null;
        }).when(requestDispatcher).include(eq(request), any(HttpServletResponse.class));

        // Page already exists
        when(pageManager.getPage("/content/tmvcs/us/en/experiences/resorts/existing-resort")).thenReturn(page);
        when(page.getPath()).thenReturn("/content/tmvcs/us/en/experiences/resorts/existing-resort");
        when(page.getContentResource()).thenReturn(contentResource);
        when(contentResource.adaptTo(ModifiableValueMap.class)).thenReturn(modifiableValueMap);

        servlet.doGet(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("Page already exists"));
    }

    @Test
    void testDoGet_ResortWithNullSlug() throws Exception {
        Set<String> runModes = new HashSet<>();
        runModes.add("author");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(request.getParameter("parentPath")).thenReturn(null);
        when(request.getParameter("template")).thenReturn(null);
        when(request.getRequestDispatcher("/bin/mvw/resort-list")).thenReturn(requestDispatcher);
        
        // Resort with null slug should be skipped
        String resortsJson = "{\"resorts\":[{\"slug\":null,\"marshaCode\":\"ABC\"}]}";
        doAnswer(invocation -> {
            HttpServletResponse respWrapper = invocation.getArgument(1);
            PrintWriter writer = respWrapper.getWriter();
            writer.write(resortsJson);
            writer.flush();
            return null;
        }).when(requestDispatcher).include(eq(request), any(HttpServletResponse.class));

        servlet.doGet(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("All pages processed"));
    }

    @Test
    void testDoGet_ResortWithMissingSlug() throws Exception {
        Set<String> runModes = new HashSet<>();
        runModes.add("author");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(request.getParameter("parentPath")).thenReturn(null);
        when(request.getParameter("template")).thenReturn(null);
        when(request.getRequestDispatcher("/bin/mvw/resort-list")).thenReturn(requestDispatcher);
        
        // Resort without slug field should be skipped
        String resortsJson = "{\"resorts\":[{\"marshaCode\":\"ABC\"}]}";
        doAnswer(invocation -> {
            HttpServletResponse respWrapper = invocation.getArgument(1);
            PrintWriter writer = respWrapper.getWriter();
            writer.write(resortsJson);
            writer.flush();
            return null;
        }).when(requestDispatcher).include(eq(request), any(HttpServletResponse.class));

        servlet.doGet(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("All pages processed"));
    }

    @Test
    void testDoGet_ResortWithNullCodes() throws Exception {
        Set<String> runModes = new HashSet<>();
        runModes.add("author");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(request.getParameter("parentPath")).thenReturn(null);
        when(request.getParameter("template")).thenReturn(null);
        when(request.getRequestDispatcher("/bin/mvw/resort-list")).thenReturn(requestDispatcher);
        
        // Resort with null marshaCode and universalPropertyCode
        String resortsJson = "{\"resorts\":[{\"slug\":\"test-resort\",\"marshaCode\":null,\"universalPropertyCode\":null}]}";
        doAnswer(invocation -> {
            HttpServletResponse respWrapper = invocation.getArgument(1);
            PrintWriter writer = respWrapper.getWriter();
            writer.write(resortsJson);
            writer.flush();
            return null;
        }).when(requestDispatcher).include(eq(request), any(HttpServletResponse.class));

        when(pageManager.getPage(anyString())).thenReturn(page);
        when(page.getPath()).thenReturn("/content/tmvcs/us/en/experiences/resorts/test-resort");
        when(page.getContentResource()).thenReturn(contentResource);
        when(contentResource.adaptTo(ModifiableValueMap.class)).thenReturn(modifiableValueMap);

        servlet.doGet(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("Page already exists"));
    }

    @Test
    void testDoGet_NullContentResource() throws Exception {
        Set<String> runModes = new HashSet<>();
        runModes.add("author");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(request.getParameter("parentPath")).thenReturn(null);
        when(request.getParameter("template")).thenReturn(null);
        when(request.getRequestDispatcher("/bin/mvw/resort-list")).thenReturn(requestDispatcher);
        
        String resortsJson = "{\"resorts\":[{\"slug\":\"test-resort\",\"marshaCode\":\"ABC\",\"universalPropertyCode\":\"UPC\"}]}";
        doAnswer(invocation -> {
            HttpServletResponse respWrapper = invocation.getArgument(1);
            PrintWriter writer = respWrapper.getWriter();
            writer.write(resortsJson);
            writer.flush();
            return null;
        }).when(requestDispatcher).include(eq(request), any(HttpServletResponse.class));

        when(pageManager.getPage(anyString())).thenReturn(page);
        when(page.getPath()).thenReturn("/content/tmvcs/us/en/experiences/resorts/test-resort");
        when(page.getContentResource()).thenReturn(null);

        servlet.doGet(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("All pages processed"));
    }

    @Test
    void testDoGet_NullModifiableValueMap() throws Exception {
        Set<String> runModes = new HashSet<>();
        runModes.add("author");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(request.getParameter("parentPath")).thenReturn(null);
        when(request.getParameter("template")).thenReturn(null);
        when(request.getRequestDispatcher("/bin/mvw/resort-list")).thenReturn(requestDispatcher);
        
        String resortsJson = "{\"resorts\":[{\"slug\":\"test-resort\",\"marshaCode\":\"ABC\",\"universalPropertyCode\":\"UPC\"}]}";
        doAnswer(invocation -> {
            HttpServletResponse respWrapper = invocation.getArgument(1);
            PrintWriter writer = respWrapper.getWriter();
            writer.write(resortsJson);
            writer.flush();
            return null;
        }).when(requestDispatcher).include(eq(request), any(HttpServletResponse.class));

        when(pageManager.getPage(anyString())).thenReturn(page);
        when(page.getPath()).thenReturn("/content/tmvcs/us/en/experiences/resorts/test-resort");
        when(page.getContentResource()).thenReturn(contentResource);
        when(contentResource.adaptTo(ModifiableValueMap.class)).thenReturn(null);

        servlet.doGet(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("All pages processed"));
    }

    @Test
    void testDoGet_ExceptionDuringProcessing() throws Exception {
        Set<String> runModes = new HashSet<>();
        runModes.add("author");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(request.getParameter("parentPath")).thenReturn(null);
        when(request.getParameter("template")).thenReturn(null);
        when(request.getRequestDispatcher("/bin/mvw/resort-list")).thenReturn(requestDispatcher);
        
        String resortsJson = "{\"resorts\":[{\"slug\":\"test-resort\"}]}";
        doAnswer(invocation -> {
            HttpServletResponse respWrapper = invocation.getArgument(1);
            PrintWriter writer = respWrapper.getWriter();
            writer.write(resortsJson);
            writer.flush();
            return null;
        }).when(requestDispatcher).include(eq(request), any(HttpServletResponse.class));

        when(pageManager.getPage(anyString())).thenReturn(null);
        when(pageManager.create(anyString(), anyString(), anyString(), anyString()))
            .thenThrow(new RuntimeException("Creation failed"));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("Error"));
    }

    @Test
    void testDoGet_MultipleResorts() throws Exception {
        Set<String> runModes = new HashSet<>();
        runModes.add("author");
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(request.getParameter("parentPath")).thenReturn("/content/custom");
        when(request.getParameter("template")).thenReturn("/conf/custom/template");
        when(request.getRequestDispatcher("/bin/mvw/resort-list")).thenReturn(requestDispatcher);
        
        String resortsJson = "{\"resorts\":[" +
            "{\"slug\":\"resort-one\",\"marshaCode\":\"M1\",\"universalPropertyCode\":\"U1\"}," +
            "{\"slug\":\"resort-two\",\"marshaCode\":\"M2\",\"universalPropertyCode\":\"U2\"}" +
            "]}";
        doAnswer(invocation -> {
            HttpServletResponse respWrapper = invocation.getArgument(1);
            PrintWriter writer = respWrapper.getWriter();
            writer.write(resortsJson);
            writer.flush();
            return null;
        }).when(requestDispatcher).include(eq(request), any(HttpServletResponse.class));

        when(pageManager.getPage("/content/custom/resort-one")).thenReturn(null);
        when(pageManager.getPage("/content/custom/resort-two")).thenReturn(page);
        when(pageManager.create(eq("/content/custom"), eq("resort-one"), anyString(), anyString())).thenReturn(page);
        when(page.getPath()).thenReturn("/content/custom/resort-one").thenReturn("/content/custom/resort-two");
        when(page.getContentResource()).thenReturn(contentResource);
        when(contentResource.adaptTo(ModifiableValueMap.class)).thenReturn(modifiableValueMap);

        servlet.doGet(request, response);

        printWriter.flush();
        String output = stringWriter.toString();
        assertTrue(output.contains("All pages processed"));
    }
}
