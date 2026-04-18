package com.mvw.core.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.DestinationLandingPageResponse;
import com.mvw.core.services.DestinationLandingService;
import com.mvw.core.services.impl.JahiaApiConfigServiceImpl;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

@Component(service = Servlet.class)
@SlingServletResourceTypes(
        resourceTypes = "mvw/components/tmvc/components/destinationListingPage",
        methods = HttpConstants.METHOD_GET,
        selectors = "data",
        extensions = "json")
public class DestinationLandingPageServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(DestinationLandingPageServlet.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Reference
    private transient DestinationLandingService destinationLandingService;

    @Reference
    private transient JahiaApiConfigServiceImpl jahiaApiConfigServiceImpl;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {

            LOGGER.info("Destination Landing API called");

            DestinationLandingPageResponse destinationDetails =
                    destinationLandingService.getDestinationResponse();

            //  Apply Base Image Path
            if (destinationDetails != null) {
                destinationDetails.applyBaseImagePath(
                        jahiaApiConfigServiceImpl.getApiImagePath()
                );
            }

            response.setStatus(HttpServletResponse.SC_OK);
            MAPPER.writeValue(response.getWriter(), destinationDetails);

            LOGGER.info("Destination Landing API success");

        } catch (Exception e) {

            LOGGER.error("Destination Landing API failed", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            try {
                response.getWriter().write("{\"error\":\"Failed to fetch destinations\"}");
            } catch (Exception ioException) {
                LOGGER.error("Error writing error response", ioException);
            }
        }
    }
}
