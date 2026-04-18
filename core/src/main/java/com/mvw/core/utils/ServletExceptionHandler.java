package com.mvw.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Collections;

public final class ServletExceptionHandler {

    private static final ObjectMapper mapper = new ObjectMapper();

    private ServletExceptionHandler() {
        // Utility class
    }

    /**
     * Handles exceptions in servlets consistently.
     */
    public static void handleException(SlingHttpServletResponse response,
                                       Logger logger,
                                       String correlationId,
                                       Exception e,
                                       int statusCode,
                                       String message) throws IOException {

        logger.error("CorrelationId: {} | {} - {}", correlationId, message, e.getMessage(), e);

        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        mapper.writeValue(response.getWriter(), Collections.singletonMap("message", message));
    }

    /**
     * Handles errors with a custom message without exception object.
     */
     public static void sendError(SlingHttpServletResponse response,
                                 int statusCode,
                                 String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getWriter(), Collections.singletonMap("error", message));
    }
}