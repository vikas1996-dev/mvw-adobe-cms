package com.mvw.core.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.models.dto.CountryDTO;
import com.mvw.core.services.CountryService;
import com.mvw.core.services.impl.MuleConfigServiceImpl;

import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/mvw/location",
                "sling.servlet.methods=GET"
        }
)
public class LocationServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(LocationServlet.class);

    @Reference
    private CountryService countryService;


    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response)
            throws IOException {

        String correlationId = UUID.randomUUID().toString();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {

            String countryCode = request.getParameter("country");

            LOGGER.info("CorrelationId: {} | LocationServlet called with country: {}",
                    correlationId, countryCode);

            if (countryCode != null) {

                if (countryCode.trim().isEmpty()) {
                    LOGGER.warn("CorrelationId: {} | Empty country parameter",
                            correlationId);
                    sendError(response,
                            HttpServletResponse.SC_BAD_REQUEST,
                            "Country parameter cannot be empty");
                    return;
                }

                List<CountryDTO> states = countryService.getStates(countryCode);

                if (states == null || states.get(0).getStates().isEmpty()) {
                    LOGGER.warn("CorrelationId: {} | No states found for {}",
                            correlationId, countryCode);
                    sendError(response,
                            HttpServletResponse.SC_NO_CONTENT,
                            "No states found for given country");
                    return;
                }

                LOGGER.info("CorrelationId: {} | Returning {} states",
                        correlationId, states.size());

                response.setStatus(HttpServletResponse.SC_OK);
                mapper.writeValue(response.getWriter(), states);

            } else {

                List<CountryDTO> countries =
                        countryService.getCountries();

                if (countries == null || countries.isEmpty()) {
                    LOGGER.warn("CorrelationId: {} | No countries found",
                            correlationId);
                    sendError(response,
                            HttpServletResponse.SC_NOT_FOUND,
                            "No countries found");
                    return;
                }

                LOGGER.info("CorrelationId: {} | Returning {} countries",
                        correlationId, countries.size());

                response.setStatus(HttpServletResponse.SC_OK);
                mapper.writeValue(response.getWriter(), countries);
            }

        } catch (IllegalArgumentException e) {

            LOGGER.error("CorrelationId: {} | Bad Request: {}",
                    correlationId, e.getMessage(), e);

            sendError(response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage());

        } catch (SocketTimeoutException e) {

            LOGGER.error("CorrelationId: {} | Gateway Timeout",
                    correlationId, e);

            sendError(response,
                    HttpServletResponse.SC_GATEWAY_TIMEOUT,
                    "Upstream service timeout");

        } catch (ConnectException e) {

            LOGGER.error("CorrelationId: {} | Service Unavailable",
                    correlationId, e);

            sendError(response,
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "External service unavailable");

        } catch (Exception e) {

            LOGGER.error("CorrelationId: {} | Internal Server Error",
                    correlationId, e);

            sendError(response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to fetch data due to server error");
        }
    }

    /**
     * Common method to send JSON error response
     */
    private void sendError(SlingHttpServletResponse response,
                           int statusCode,
                           String message) throws IOException {

        response.setStatus(statusCode);

        mapper.writeValue(response.getWriter(),
                Collections.singletonMap("message", message));
    }
}