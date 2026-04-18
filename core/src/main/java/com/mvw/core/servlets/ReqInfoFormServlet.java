package com.mvw.core.servlets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mvw.core.constants.AppConstants;
import com.mvw.core.services.CountryRegionService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = { Servlet.class })
@SlingServletPaths(value = "/bin/mvw/request-info")
public class ReqInfoFormServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ReqInfoFormServlet.class);

    @Reference
    private transient CountryRegionService countryRegionService;

    @Override
    protected void doPost(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {

        String message = req.getParameter("message");
        String finalRedirectUrl = AppConstants.TMVC_BASE_CONTENT_PATH + "/error/error-page.html";
        LOG.info("message: {}", message);

        if (message != null && !message.isEmpty()) {
            try {
                JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

                if (jsonObject.has("Country")) {
                    String country = jsonObject.get("Country").getAsString();

                    String requestType = "";
                    if (jsonObject.has("formId") && !jsonObject.get("formId").isJsonNull()) {
                        requestType = jsonObject.get("formId").getAsString();
                    }

                    String action = "";
                    if (jsonObject.has("action") && !jsonObject.get("action").isJsonNull()) {
                        action = jsonObject.get("action").getAsString();
                    }

                    LOG.info("Extracted country: {}", country);
                    LOG.info("RequestType: {}", requestType);
                    LOG.info("Action: {}", action);

                    boolean isSpecialOffers = "web_to_lead".equalsIgnoreCase(action);

                    if (AppConstants.COUNTRY_USA.equalsIgnoreCase(country)) {
                        if (isSpecialOffers) {
                            finalRedirectUrl = AppConstants.TMVC_BASE_CONTENT_PATH + "/special-offers/special-offers-thank-you.html";

                        } else if ("Owner".equalsIgnoreCase(requestType)) {
                            finalRedirectUrl = AppConstants.TMVC_BASE_CONTENT_PATH + "/request-information/request-information-thank-you-owner.html";

                        } else if ("Owner Services".equalsIgnoreCase(requestType)) {
                            finalRedirectUrl = AppConstants.TMVC_BASE_CONTENT_PATH + "/request-information/request-information-thank-you-owner-services.html";

                        } else {
                            finalRedirectUrl = AppConstants.TMVC_BASE_CONTENT_PATH + "/request-information/request-information-thank-you.html";
                        }
                    }else {
                        String basePath = AppConstants.TMVC_BASE_CONTENT_PATH + "/request-information/no-offer-redirect";

                        String region = countryRegionService.getRegionByCountry(country);

                        if (region != null) {
                            String[] redirectUrls = {
                                    String.format("%s/%s/%s.html", basePath, region, country),
                                    String.format("%s/%s.html", basePath, region),
                                    basePath + ".html"
                            };

                            ResourceResolver resolver = req.getResourceResolver();
                            for (String url : redirectUrls) {
                                LOG.info("Checking URL: {}", url);
                                Resource resource = resolver.resolve(req, url);
                                if (resource != null && !Resource.RESOURCE_TYPE_NON_EXISTING.equals(resource.getResourceType())) {
                                    finalRedirectUrl = url + "?country=" + country;
                                    LOG.info("finalRedirectUrl: {}", finalRedirectUrl);
                                    break;
                                }
                            }
                        } else {
                            finalRedirectUrl = basePath + ".html?country=" + country;
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error("Error parsing message JSON", e);
                finalRedirectUrl = AppConstants.TMVC_BASE_CONTENT_PATH + "/error/error-page.html";
            }
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("status", "success");
        responseJson.addProperty("redirectUrl", finalRedirectUrl);

        resp.getWriter().write(responseJson.toString());
    }
}