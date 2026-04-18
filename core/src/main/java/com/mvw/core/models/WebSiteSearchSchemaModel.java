package com.mvw.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.Externalizer;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sling Model for generating JSON-LD schema for the WebSite SearchAction.
 * Produces structured data that enables the Google Sitelinks Search Box.
 * For destinations, resorts, and other listing pages the schema name and URL
 * are derived from the current page (dynamic); otherwise falls back to site defaults.
 *
 * <p>Schema.org type: {@code WebSite} with a {@code SearchAction} potentialAction.
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class WebSiteSearchSchemaModel {

    private static final Logger LOG = LoggerFactory.getLogger(WebSiteSearchSchemaModel.class);

    private static final String SITE_NAME = "MarriottVacationClubs.com";
    private static final String SITE_URL = "https://www.marriottvacationclubs.com";
    private static final String SEARCH_TARGET_TEMPLATE =
            "https://www.marriottvacationclubs.com/experiences/resorts.html?search={search_term_string}";
    private static final String QUERY_INPUT = "required maxlength=100 name=search";

    @Self
    private SlingHttpServletRequest request;

    @OSGiService
    private Externalizer externalizer;

    private String jsonLD;

    @PostConstruct
    protected void init() {
        try {
            Map<String, Object> schema = buildSchema();
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            this.jsonLD = gson.toJson(schema);
        } catch (Exception e) {
            LOG.error("Failed to initialize WebSiteSearchSchemaModel", e);
            this.jsonLD = "{}";
        }
    }

    private Map<String, Object> buildSchema() {
        String pageName = SITE_NAME;
        String pageUrl = SITE_URL;
        String searchTarget = SEARCH_TARGET_TEMPLATE;

        if (request != null) {
            ResourceResolver resolver = request.getResourceResolver();
            PageManager pageManager = resolver != null ? resolver.adaptTo(PageManager.class) : null;
            if (pageManager != null) {
                Page page = pageManager.getContainingPage(request.getResource());
                if (page != null) {
                    String title = page.getTitle();
                    if (title != null && !title.trim().isEmpty()) {
                        pageName = title.trim();
                    }
                    if (externalizer != null && resolver != null) {
                        try {
                            String path = page.getPath();
                            if (!path.endsWith(".html")) {
                                path = path + ".html";
                            }
                            String externalized = externalizer.externalLink(resolver, "tmvcs", path);
                            if (externalized != null && !externalized.isEmpty()) {
                                pageUrl = externalized;
                                searchTarget = externalized + (externalized.contains("?") ? "&" : "?")
                                        + "search={search_term_string}";
                            }
                        } catch (Exception e) {
                            LOG.debug("Could not externalize page path for schema", e);
                        }
                    }
                }
            }
        }

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("@context", "https://schema.org");
        schema.put("@type", "WebSite");
        schema.put("name", pageName);
        schema.put("url", pageUrl);

        Map<String, Object> searchAction = new LinkedHashMap<>();
        searchAction.put("@type", "SearchAction");
        searchAction.put("target", searchTarget);
        searchAction.put("query-input", QUERY_INPUT);

        schema.put("potentialAction", searchAction);

        return schema;
    }

    /**
     * Returns the generated JSON-LD string for the WebSite SearchAction schema.
     *
     * @return the JSON-LD string
     */
    public String getJsonLD() {
        return jsonLD;
    }
}
