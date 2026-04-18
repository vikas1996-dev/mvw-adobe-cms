package com.mvw.core.servlets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import com.mvw.core.constants.AppConstants;
import com.mvw.core.services.CountryRegionService;
import com.mvw.core.services.impl.MuleConfigServiceImpl;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.util.EntityUtils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.*;
import java.util.UUID;

@Component(service = Servlet.class, property = {
        "sling.servlet.paths=/bin/mvw/submitLead",
        "sling.servlet.methods=POST"
})
public class LeadSubmitServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LeadSubmitServlet.class);

    @Reference
    private transient CountryRegionService countryRegionService;

    @Reference
    private MuleConfigServiceImpl muleConfigService;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected void doPost(SlingHttpServletRequest request,
                          SlingHttpServletResponse response) throws IOException {

        String correlationId = UUID.randomUUID().toString();

        response.setContentType(AppConstants.CONTENTTYPE_APPLICATION_JSON);
        response.setCharacterEncoding("UTF-8");

        String caseUrl = muleConfigService.getCaseUrl();
        String leadUrl = muleConfigService.getLeadUrl();
        String jwtToken = muleConfigService.getJwtToken();

        try {
            String type = sanitize(request.getParameter("type"));

            if (type == null || type.isEmpty()) {
                writeError(response, "Type parameter is required", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            String jsonPayload = readRequestBody(request);
            if (jsonPayload.isEmpty()) {
                writeError(response, "Request body cannot be empty", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            JsonNode rootNode = OBJECT_MAPPER.readTree(jsonPayload);

            String countryCode = sanitize(rootNode.path("countryCode").asText(null));
            String country = sanitize(rootNode.path("country").asText(null));
            String requestType = sanitize(rootNode.path("formId").asText(""));

            boolean isSpecialOffers =
                    "lead".equalsIgnoreCase(type) &&
                            (requestType == null || requestType.isEmpty());

            // NON-USA FLOW
            if (countryCode != null && !"USA".equalsIgnoreCase(countryCode)) {
                String redirectUrl = escape(buildRegionRedirectUrl(country));

                JsonObject json = new JsonObject();
                json.addProperty("status", "success");
                json.addProperty("redirectUrl", redirectUrl);

                writeJson(response, json);
                return;
            }

            // URL selection
            String finalUrl;
            if ("lead".equalsIgnoreCase(type)) {
                finalUrl = leadUrl;
            } else if ("case".equalsIgnoreCase(type)) {
                finalUrl = caseUrl;
            } else {
                writeError(response, "Invalid type", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }


            // Build payload safely
            String safePayload;
            if ("lead".equalsIgnoreCase(type)) {
                safePayload = buildLeadPayload(rootNode);
            } else {
                safePayload = buildCasePayload(rootNode);
            }

            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .setSocketTimeout(8000)
                    .build();

            try (CloseableHttpClient client = HttpClients.custom()
                    .setDefaultRequestConfig(config)
                    .build()) {

                HttpPost post = new HttpPost(finalUrl);
                post.setHeader("Content-Type", "application/json");
                post.setHeader("Accept", "application/json");
                post.setHeader("x-correlation-id", correlationId);

                if (jwtToken != null && !jwtToken.isEmpty()) {
                    post.setHeader("AXIS-AUTH", jwtToken);
                }

                post.setEntity(new StringEntity(safePayload, "UTF-8"));

                try (CloseableHttpResponse apiResponse = client.execute(post)) {
                    int statusCode = apiResponse.getStatusLine().getStatusCode();
                    String apiResponseStr = apiResponse.getEntity() != null
                            ? escape(EntityUtils.toString(apiResponse.getEntity()))
                            : "";

                    if (statusCode >= 500) {
                        writeError(response, apiResponseStr, HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                        return;
                    }

                    String redirectUrl = escape(buildUsaRedirectUrl(requestType, isSpecialOffers));

                    JsonObject json = new JsonObject();
                    json.addProperty("status", "success");
                    json.addProperty("redirectUrl", redirectUrl);
                    json.addProperty("response", apiResponseStr);

                    writeJson(response, json);
                }
            }

        } catch (ConnectTimeoutException | ConnectException e) {
            writeError(response, "Service unavailable", HttpServletResponse.SC_SERVICE_UNAVAILABLE);

        } catch (Exception e) {
            LOGGER.error("Error", e);
            writeError(response, "Internal Server Error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // ================= PAYLOAD BUILDERS =================

    private String buildLeadPayload(JsonNode root) {
        ObjectNode safe = OBJECT_MAPPER.createObjectNode();

        add(root, safe, "firstName");
        add(root, safe, "lastName");
        add(root, safe, "email");
        addPhone(root, safe); // handle phone correctly
        add(root, safe, "countryCode");
        add(root, safe, "stateCode");
        add(root, safe, "postalCode");
        add(root, safe, "mktSourceSystem");
        add(root, safe, "mktLeadOriginCode");
        add(root, safe, "mktInbndOutbndInd");
        add(root, safe, "leadSource");

        return safe.toString();
    }

    private String buildCasePayload(JsonNode root) {
        ObjectNode safe = OBJECT_MAPPER.createObjectNode();

        add(root, safe, "name");
        addPhone(root, safe); // handle phone correctly
        add(root, safe, "email");
        add(root, safe, "subject");
        add(root, safe, "type");
        add(root, safe, "priority");
        add(root, safe, "description");
        add(root, safe, "reason");
        add(root, safe, "reasonDetail");
        add(root, safe, "origin");

        if (root.has("urlSource")) {
            String url = sanitize(root.get("urlSource").asText());
            if (url.startsWith("https://")) {
                safe.put("urlSource", url);
            }
        }

        return safe.toString();
    }

    private void add(JsonNode root, ObjectNode safe, String field) {
        if (!root.has(field) || root.get(field).isNull()) return;

        JsonNode node = root.get(field);
        if (node.isNumber()) {
            safe.put(field, node.longValue());
        } else if (node.isBoolean()) {
            safe.put(field, node.asBoolean());
        } else {
            safe.put(field, sanitize(node.asText()));
        }
    }

    private void addPhone(JsonNode root, ObjectNode safe) {
        if (root.has("phone") && !root.get("phone").isNull()) {
            String phoneStr = sanitize(root.get("phone").asText());

            if (phoneStr.matches("\\d{7,15}")) { // validate only digits
                try {
                    long phoneLong = Long.parseLong(phoneStr);
                    safe.put("phone", phoneLong);
                } catch (NumberFormatException e) {
                    LOGGER.warn("Invalid phone number: {}", phoneStr);
                }
            } else {
                LOGGER.warn("Phone failed validation: {}", phoneStr);
            }
        }
    }

    private String sanitize(String input) {
        if (input == null) return "";
        return input.replaceAll("[\\r\\n<>]", "").trim();
    }

    private String escape(String input) {
        return input == null ? "" : StringEscapeUtils.escapeHtml4(input);
    }

    // ================= HELPERS =================

    private String readRequestBody(SlingHttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString().replaceAll("[\\r\\n]", "");
    }

    private void writeJson(SlingHttpServletResponse response, JsonObject json) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(json.toString());
    }

    private void writeError(SlingHttpServletResponse response, String message, int status) throws IOException {
        JsonObject error = new JsonObject();
        error.addProperty("error", escape(message));
        response.setStatus(status);
        response.getWriter().write(error.toString());
    }

    private String buildRegionRedirectUrl(String country) {
        String base = AppConstants.TMVC_BASE_CONTENT_PATH + "/request-information/no-offer-redirect";
        String region = countryRegionService.getRegionByCountry(country);
        return base + "/" + region + ".html?country=" + country;
    }

    private String buildUsaRedirectUrl(String requestType, boolean isSpecialOffers) {
        String base = AppConstants.TMVC_BASE_CONTENT_PATH + "/request-information";

        if (isSpecialOffers) {
            return base + "/request-information-thank-you.html";
        } else if ("Owner".equalsIgnoreCase(requestType)) {
            return base + "/request-information-thank-you-owner.html";
        } else if ("Owner Services".equalsIgnoreCase(requestType)) {
            return base + "/request-information-thank-you-owner-services.html";
        } else {
            return base + "/request-information-thank-you.html";
        }
    }
}