package com.mvw.core.servlets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.mvw.core.models.dto.TripAdvisorDto;
import com.mvw.core.models.dto.TripAdvisorResponseDto;
import com.mvw.core.services.TripAdvisorListService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TripAdvisorListServletTest {

    @InjectMocks
    private TripAdvisorListServlet servlet;

    @Mock
    private TripAdvisorListService tripAdvisorListService;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private SlingHttpServletResponse response;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testDoGet_Success() throws Exception {
        List<TripAdvisorDto> mockList = new ArrayList<>();
        TripAdvisorDto dto = new TripAdvisorDto();
        dto.setRating("4.5");
        dto.setReviews("100");
        mockList.add(dto);

        TripAdvisorResponseDto mockResponse = new TripAdvisorResponseDto();
        mockResponse.setList(mockList);
        mockResponse.setCount(1);

        when(tripAdvisorListService.getResponseList()).thenReturn(mockResponse);

        servlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testDoGet_IOException() throws Exception {
        when(tripAdvisorListService.getResponseList()).thenReturn(new TripAdvisorResponseDto());
        when(response.getWriter()).thenThrow(new IOException("IO Error"));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    void testDoGet_GeneralException() throws Exception {
        when(tripAdvisorListService.getResponseList()).thenThrow(new RuntimeException("General Error"));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    void testDoGet_NullResponse() throws Exception {
        when(tripAdvisorListService.getResponseList()).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}
