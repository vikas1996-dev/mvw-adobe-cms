package com.mvw.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.services.JahiaClientService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.http.HttpResponse;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {
        ResortPageModel.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ResortPageModel {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @SlingObject
    private Resource resource;

    @SlingObject
    private SlingHttpServletRequest request;
    
    @OSGiService
    private JahiaClientService jahiaClientService;

    private HttpResponse<String> jahiaResponse;

    @PostConstruct
    protected void init() {
        try {
            // Get UPC code from page properties, fallback to default
            String upcCode = getUpcCode();

            logger.info("Fetching Jahia data for UPC: {}", upcCode);

            // Call Jahia GraphQL API
            jahiaResponse = jahiaClientService.callGraphqlService(upcCode);

            // Check if response is valid
            if (jahiaResponse != null && jahiaResponse.statusCode() == 200) {
                // Parse JSON response
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(jahiaResponse.body());

                // Set as request attribute so all components can access it
                request.setAttribute("jahiaResponse", root);

                logger.info("Successfully set jahiaResponse for UPC: {}", upcCode);
            } else {
                logger.error("Failed to fetch Jahia data. Status code: {}",
                        jahiaResponse != null ? jahiaResponse.statusCode() : "null");
            }

        } catch (IOException e) {
            logger.error("IOException in ResortPageModel.init(): {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error in ResortPageModel.init(): {}", e.getMessage(), e);
        }
    }

    /**
     * Get UPC code from page properties or return default
     */
    private String getUpcCode() {
        try {
            PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);

            if (pageManager == null) {
                return null;
            }

            Page currentPage = pageManager.getContainingPage(resource);

            if (currentPage == null) {
                return null;
            }

            String pageUpcCode = currentPage.getProperties().get("upcCode", String.class);

            if (pageUpcCode != null && !pageUpcCode.trim().isEmpty()) {
                return pageUpcCode.trim();
            }
            return null;

        } catch (Exception e) {
            return null;
        }
    }

}