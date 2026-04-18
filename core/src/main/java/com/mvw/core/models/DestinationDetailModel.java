package com.mvw.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvw.core.services.DestinationsDetailsService;
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
        DestinationDetailModel.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DestinationDetailModel {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @SlingObject
    private Resource resource;

    @SlingObject
    private SlingHttpServletRequest request;

    @OSGiService
    private DestinationsDetailsService destinationsDetailsService;

    @PostConstruct
    protected void init() {
        HttpResponse<String> response;
        try {
            // Get UPC code from page properties, fallback to default
            String upcCode = getName();

            response = destinationsDetailsService.callGraphqlService(upcCode);

            if (response != null && response.statusCode() == 200) {
                // Parse JSON response
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.body());

                request.setAttribute("destinationResponse", root);
                request.setAttribute("nodeName", upcCode);
                logger.info("Successfully set for UPC: {}", upcCode);
            } else {
                logger.error("Failed to fetch data. Status code: {}",
                        response != null ? response.statusCode() : "null");
            }

        } catch (IOException e) {
            logger.error("IOException in DestinationResponse.init(): {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error in DestinationResponse.init(): {}", e.getMessage(), e);
        }
    }

    /**
     * Get UPC code from page properties or return default
     */
    private String getName() {
        try {
            PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);

            if (pageManager == null) {
                return null;
            }

            Page currentPage = pageManager.getContainingPage(resource);

            if (currentPage == null) {
                return null;
            }

            String pageUpcCode = currentPage.getProperties().get("name", String.class);

            if (pageUpcCode != null && !pageUpcCode.trim().isEmpty()) {
                return pageUpcCode.trim();
            }
            return null;

        } catch (Exception e) {
            return null;
        }
    }

}