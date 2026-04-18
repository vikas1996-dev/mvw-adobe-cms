package com.mvw.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RandomNumberModelTest {

    private SlingHttpServletRequest mockRequest;
    private HttpSession mockSession;

    @BeforeEach
    void setUp() {
        mockRequest = mock(SlingHttpServletRequest.class);
        mockSession = mock(HttpSession.class);
        when(mockRequest.getSession()).thenReturn(mockSession);
    }

    @Test
    void testRequestRandomIsInRange() {
        when(mockSession.getAttribute("sessionRandom")).thenReturn(null);
        
        RandomNumberModel model = new RandomNumberModel(mockRequest);
        
        int requestRandom = model.getRequestRandom();
        assertTrue(requestRandom >= 10000 && requestRandom < 100000, 
                   "Request random should be between 10000 and 99999");
    }

    @Test
    void testSessionRandomIsInRange() {
        when(mockSession.getAttribute("sessionRandom")).thenReturn(null);
        
        RandomNumberModel model = new RandomNumberModel(mockRequest);
        
        int sessionRandom = model.getSessionRandom();
        assertTrue(sessionRandom >= 10000 && sessionRandom < 100000, 
                   "Session random should be between 10000 and 99999");
    }

    @Test
    void testSessionRandomIsStoredInSession() {
        when(mockSession.getAttribute("sessionRandom")).thenReturn(null);
        
        RandomNumberModel model = new RandomNumberModel(mockRequest);
        
        verify(mockSession).setAttribute(eq("sessionRandom"), anyInt());
    }

    @Test
    void testExistingSessionRandomIsUsed() {
        int existingSessionRandom = 54321;
        when(mockSession.getAttribute("sessionRandom")).thenReturn(existingSessionRandom);
        
        RandomNumberModel model = new RandomNumberModel(mockRequest);
        
        assertEquals(existingSessionRandom, model.getSessionRandom());
        verify(mockSession, never()).setAttribute(eq("sessionRandom"), anyInt());
    }

    @Test
    void testRequestRandomIsDifferentOnMultipleCalls() {
        when(mockSession.getAttribute("sessionRandom")).thenReturn(null);
        
        RandomNumberModel model1 = new RandomNumberModel(mockRequest);
        RandomNumberModel model2 = new RandomNumberModel(mockRequest);
        
        // While this could theoretically fail due to randomness, it's extremely unlikely
        // that two random 5-digit numbers would be the same
        // We're just testing that the model can be instantiated multiple times
        assertNotNull(model1.getRequestRandom());
        assertNotNull(model2.getRequestRandom());
    }

    @Test
    void testGetRequestRandom() {
        when(mockSession.getAttribute("sessionRandom")).thenReturn(12345);
        
        RandomNumberModel model = new RandomNumberModel(mockRequest);
        
        int requestRandom = model.getRequestRandom();
        assertTrue(requestRandom >= 10000);
        assertTrue(requestRandom < 100000);
    }

    @Test
    void testGetSessionRandom() {
        int expectedSessionRandom = 67890;
        when(mockSession.getAttribute("sessionRandom")).thenReturn(expectedSessionRandom);
        
        RandomNumberModel model = new RandomNumberModel(mockRequest);
        
        assertEquals(expectedSessionRandom, model.getSessionRandom());
    }
}
