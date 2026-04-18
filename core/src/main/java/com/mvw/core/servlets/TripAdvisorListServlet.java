package com.mvw.core.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.TripAdvisorResponseDto;
import com.mvw.core.services.TripAdvisorListService;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/mvw/tripadvisor")
public class TripAdvisorListServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(TripAdvisorListServlet.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Reference
    private transient TripAdvisorListService tripAdvisorListService;

   @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            TripAdvisorResponseDto list = tripAdvisorListService.getResponseList();
            response.setStatus(HttpServletResponse.SC_OK);
            MAPPER.writeValue(response.getWriter(), list);

        } catch (IOException e) {
            LOGGER.error("Failed to write TripAdvisor list response", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LOGGER.error("TripAdvisor list servlet failed", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
