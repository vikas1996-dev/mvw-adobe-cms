package com.mvw.core.servlets;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VideoContentDispositionFilterTest {

    private VideoContentDispositionFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private FilterConfig filterConfig;

    @BeforeEach
    void setUp() {
        filter = new VideoContentDispositionFilter();
    }

    @Test
    void testDoFilter_Mp4Video() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/content/dam/videos/sample.mp4");

        filter.doFilter(request, response, filterChain);

        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq("Content-Disposition"), headerCaptor.capture());

        String headerValue = headerCaptor.getValue();

        assertTrue(headerValue.contains("inline"));
        assertTrue(headerValue.contains("filename*="));
        assertTrue(headerValue.contains("sample.mp4"));

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilter_MovVideo() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/content/dam/videos/sample.mov");

        filter.doFilter(request, response, filterChain);

        verify(response).setHeader(eq("Content-Disposition"), contains("sample.mov"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilter_MkvVideo() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/content/dam/videos/sample.mkv");

        filter.doFilter(request, response, filterChain);

        verify(response).setHeader(eq("Content-Disposition"), contains("sample.mkv"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilter_UpperCaseExtension() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/content/dam/videos/sample.MP4");

        filter.doFilter(request, response, filterChain);

        verify(response).setHeader(eq("Content-Disposition"), contains("sample.MP4"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilter_FileNameWithSpaces() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/content/dam/videos/sample video.mp4");

        filter.doFilter(request, response, filterChain);

        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq("Content-Disposition"), headerCaptor.capture());

        String headerValue = headerCaptor.getValue();

        // space should be encoded as %20
        assertTrue(headerValue.contains("sample%20video.mp4"));

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilter_NonVideoFile() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/content/dam/images/sample.jpg");

        filter.doFilter(request, response, filterChain);

        verify(response, never()).setHeader(eq("Content-Disposition"), anyString());
        verify(filterChain).doFilter(request, response);
    }

  

    @Test
    void testDoFilter_HtmlFile() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/content/page.html");

        filter.doFilter(request, response, filterChain);

        verify(response, never()).setHeader(eq("Content-Disposition"), anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testInit() {
        filter.init(filterConfig);
    }

    @Test
    void testDestroy() {
        filter.destroy();
    }
}