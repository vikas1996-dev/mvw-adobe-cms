package com.mvw.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FaqJsonLdModel {

    private static final Logger LOG = LoggerFactory.getLogger(FaqJsonLdModel.class);

    @Inject
    private Resource resource; // FAQ component node

    @SlingObject
    private ResourceResolver resourceResolver;

    @Inject
    private SlingSettingsService slingSettingsService;

    private String jsonLd;

    private final ObjectMapper mapper = new ObjectMapper();
    private final List<FaqItem> faqItems = new ArrayList<>();

    @PostConstruct
    protected void init() {

        if (resource == null) {
            LOG.error("Resource is null in FaqJsonLdModel");
            return;
        }

        LOG.info("Processing FAQ component at: {}", resource.getPath());

        // Iterate through all children
        Iterator<Resource> children = resource.listChildren();

        while (children.hasNext()) {
            Resource child = children.next();
            String childName = child.getName();

            LOG.debug("Checking child: {}", childName);

            // Skip CTA container and other non-item nodes
            if ("ctas".equals(childName) || childName.startsWith("jcr:")) {
                LOG.debug("Skipping node: {}", childName);
                continue;
            }

            // Accept "item_1", "item_2", etc. - any node starting with "item"
            if (childName.startsWith("item")) {
                LOG.info("Processing FAQ item: {}", childName);

                // Read question from cq:panelTitle (accordion title property)
                ValueMap itemProps = child.getValueMap();
                String question = itemProps.get("cq:panelTitle", String.class);
                // Fallback to jcr:title if cq:panelTitle is not present
                if (question == null || question.isEmpty()) {
                    question = itemProps.get("jcr:title", String.class);
                }

                LOG.debug("Question for {}: {}", childName, question);
                LOG.info("Question for child{}: {}", child, question);
                // Read answer from child text node
                String answer = extractAnswer(child);

                LOG.debug("Answer for {}: {}", childName,
                        answer != null ? (answer.substring(0, Math.min(50, answer.length())) + "...") : "null");

                // Only add if both question and answer exist
                if (question != null && !question.isEmpty() && answer != null && !answer.isEmpty()) {
                    faqItems.add(new FaqItem(question, answer));
                    LOG.info("Added FAQ item: {}", question);
                } else {
                    LOG.warn("Skipping FAQ item {} - missing question or answer", childName);
                }
            }
        }

        LOG.info("Total FAQ items found: {}", faqItems.size());

        // Build the JSON-LD structure
        buildJsonLd();
    }

    /**
     * Extract answer text from FAQ item child nodes.
     * Tries multiple possible paths where the text might be stored.
     */
    private String extractAnswer(Resource itemResource) {
        String answer = null;

        // Try path 1: /faq/item_1/copyblock (direct child)
        Resource copyResource = itemResource.getChild("copyblock");
        LOG.info("copyResource: {}", copyResource);
        if (copyResource != null) {
            ValueMap textProps = copyResource.getValueMap();
            LOG.info("textProps: {}", textProps);
            answer = textProps.get("copyText", String.class);
            LOG.info("Answer: {}", answer);
            if (answer != null && !answer.isEmpty()) {
                LOG.debug("Found answer in direct text child");
                return answer;
            }
        }

        // Try path 1: item_1/text (direct child)
        Resource textResource = itemResource.getChild("text");
        if (textResource != null) {
            ValueMap textProps = textResource.getValueMap();
            answer = textProps.get("text", String.class);

            if (answer != null && !answer.isEmpty()) {
                LOG.debug("Found answer in direct text child");
                return answer;
            }
        }

        // Try path 2: item_1/container/text (nested in container)
        Resource containerResource = itemResource.getChild("container");
        if (containerResource != null) {
            textResource = containerResource.getChild("text");
            if (textResource != null) {
                ValueMap textProps = textResource.getValueMap();
                answer = textProps.get("text", String.class);

                if (answer != null && !answer.isEmpty()) {
                    LOG.debug("Found answer in container/text");
                    return answer;
                }
            }
        }

        // Try path 3: Iterate through all children to find text component
        Iterator<Resource> children = itemResource.listChildren();
        while (children.hasNext()) {
            Resource child = children.next();

            // Skip jcr: nodes
            if (child.getName().startsWith("jcr:")) {
                continue;
            }

            ValueMap childProps = child.getValueMap();
            String resourceType = childProps.get("sling:resourceType", String.class);

            // Check if it's a text component
            if (resourceType != null && resourceType.contains("text")) {
                answer = childProps.get("text", String.class);
                if (answer != null && !answer.isEmpty()) {
                    LOG.debug("Found answer in text component: {}", child.getName());
                    return answer;
                }
            }
        }

        LOG.warn("Could not find answer text for item: {}", itemResource.getPath());
        return null;
    }

    /**
     * Build the JSON-LD structured data
     */
    private void buildJsonLd() {
        try {
            ObjectNode root = mapper.createObjectNode();

            root.put("@context", "https://schema.org");
            root.put("@type", "Article");
            root.put("headline", "FAQ");

            // Author
            ObjectNode author = mapper.createObjectNode();
            author.put("@type", "Organization");
            author.put("name", "The Marriott Vacation Clubs");
            root.set("author", author);

            // Get dates from page properties
            String datePublished = "2023-12-18T00:00:00-05:00";
            String dateModified = "2026-03-26T00:00:00-05:00";

            Resource pageResource = getPageResource();
            if (pageResource != null) {
                Resource pageContent = pageResource.getChild("jcr:content");
                if (pageContent != null) {
                    ValueMap pageProps = pageContent.getValueMap();

                    Calendar created = pageProps.get("jcr:created", Calendar.class);
                    if (created != null) {
                        datePublished = formatDate(created);
                    }

                    Calendar modified = pageProps.get("cq:lastModified", Calendar.class);
                    if (modified == null) {
                        modified = pageProps.get("jcr:lastModified", Calendar.class);
                    }
                    if (modified != null) {
                        dateModified = formatDate(modified);
                    }
                }
            }

            root.put("datePublished", datePublished);
            root.put("dateModified", dateModified);

            // mainEntityOfPage
            ObjectNode mainEntityOfPage = mapper.createObjectNode();
            mainEntityOfPage.put("@type", "WebPage");
            mainEntityOfPage.put("@id", getPageUrl());
            root.set("mainEntityOfPage", mainEntityOfPage);

            // FAQ list (hasPart)
            ArrayNode hasPart = mapper.createArrayNode();

            for (FaqItem item : faqItems) {
                ObjectNode questionNode = mapper.createObjectNode();
                questionNode.put("@type", "Question");
                questionNode.put("name", item.getQuestion());

                ObjectNode acceptedAnswer = mapper.createObjectNode();
                acceptedAnswer.put("@type", "Answer");
                acceptedAnswer.put("text", item.getAnswer());

                questionNode.set("acceptedAnswer", acceptedAnswer);
                hasPart.add(questionNode);
            }

            root.set("hasPart", hasPart);

            // Generate pretty-printed JSON
            jsonLd = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(root);

            LOG.info("Successfully generated JSON-LD with {} FAQ items", faqItems.size());

        } catch (Exception e) {
            LOG.error("Error generating JSON-LD", e);
            jsonLd = "{}";
        }
    }

    /**
     * Get the parent page resource
     */
    private Resource getPageResource() {
        if (resourceResolver == null || resource == null) {
            return null;
        }

        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager != null) {
            Page currentPage = pageManager.getContainingPage(resource);
            if (currentPage != null) {
                return currentPage.adaptTo(Resource.class);
            }
        }

        // Fallback: traverse up to find cq:Page
        Resource current = resource;
        while (current != null) {
            String resourceType = current.getResourceType();
            if ("cq:Page".equals(resourceType)) {
                return current;
            }
            current = current.getParent();
        }
        return null;
    }

    /**
     * Get the current page URL using Sling Resource Resolver mapping.
     * This respects the Sling mapping configuration and works for any domain.
     */
    private String getPageUrl() {
        if (resourceResolver == null) {
            LOG.warn("ResourceResolver is null, cannot generate page URL");
            return "";
        }

        Resource pageResource = getPageResource();
        if (pageResource == null) {
            LOG.warn("Could not find containing page resource");
            return "";
        }

        String pagePath = pageResource.getPath();
        LOG.debug("Page resource path: {}", pagePath);

        // Use ResourceResolver.map() to apply Sling mappings
        // This will handle domain mapping, vanity URLs, etc.
        String mappedPath = resourceResolver.map(pagePath);
        LOG.debug("Mapped path: {}", mappedPath);

        // If the mapped path is relative (starts with /), we need to construct full URL
        if (mappedPath.startsWith("/")) {
            // Get the externalized URL using the resource resolver
            // The map method with a request would give us the full URL, but since we don't
            // have request,
            // we'll construct it manually
            String externalUrl = getExternalizedUrl(pagePath);
            LOG.debug("Externalized URL: {}", externalUrl);
            return externalUrl;
        }

        // If map() returned an absolute URL, use it directly
        return mappedPath;
    }

    /**
     * Get externalized URL for the given path.
     * This method constructs the full URL using Sling mappings.
     */
    private String getExternalizedUrl(String path) {
        if (resourceResolver == null || path == null) {
            return "";
        }

        // Get the current page to access its properties
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager != null) {
            Page currentPage = pageManager.getContainingPage(resource);
            if (currentPage != null) {
                // Try to get the externalized URL from the page
                Resource pageContentResource = currentPage.getContentResource();
                if (pageContentResource != null) {
                    ValueMap pageProps = pageContentResource.getValueMap();

                    // Check if there's a canonical URL configured
                    String canonicalUrl = pageProps.get("canonicalUrl", String.class);
                    if (canonicalUrl != null && !canonicalUrl.isEmpty()) {
                        LOG.debug("Using canonical URL: {}", canonicalUrl);
                        return canonicalUrl;
                    }
                }
            }
        }

        // Use Sling mapping with scheme and host
        // Map the path to get the externalized form
        String mappedPath = resourceResolver.map(path);

        // If still relative, prepend with a default scheme and use the mapped path
        if (mappedPath.startsWith("/")) {
            // In production, this would be determined by your Sling mapping config
            // For now, we'll construct using the resource path mapping
            String scheme = isPublishInstance() ? "https" : "http";

            // Get domain from Sling mapping or use a sensible default
            String domain = getDomainFromMapping();

            String fullUrl = scheme + "://" + domain + mappedPath;

            // Add .html extension if not present and it's a page
            if (!fullUrl.contains(".html") && !fullUrl.endsWith("/")) {
                fullUrl += ".html";
            }

            return fullUrl;
        }

        return mappedPath;
    }

    /**
     * Determine if this is a publish instance
     */
    private boolean isPublishInstance() {
        if (slingSettingsService != null) {
            return slingSettingsService.getRunModes().contains("publish");
        }
        return false;
    }

    /**
     * Get domain from Sling mapping configuration.
     * This reads the actual Sling mapping to determine the correct domain.
     */
    private String getDomainFromMapping() {
        if (resourceResolver == null) {
            return "localhost:4502";
        }

        // Try to map a known content path and extract domain from it
        String testPath = "/content/mvw";
        String mapped = resourceResolver.map(testPath);

        // If the mapped path contains a domain (http:// or https://)
        if (mapped.startsWith("http://") || mapped.startsWith("https://")) {
            try {
                java.net.URL url = new java.net.URL(mapped);
                String host = url.getHost();
                int port = url.getPort();

                if (port != -1 && port != 80 && port != 443) {
                    return host + ":" + port;
                }
                return host;
            } catch (Exception e) {
                LOG.error("Error parsing mapped URL: {}", mapped, e);
            }
        }

        // Fallback: try to get from resource resolver's map method with a dummy
        // external request
        // In a real scenario, the Sling mapping configuration would handle this

        // Default fallback based on environment
        if (isPublishInstance()) {
            return "www.marriottvacationclubs.com";
        }

        return "localhost:4502";
    }

    /**
     * Format Calendar to ISO 8601 format
     */
    private String formatDate(Calendar calendar) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        return sdf.format(calendar.getTime());
    }

    public String getJsonLd() {
        return jsonLd != null ? jsonLd : "{}";
    }

    /**
     * Inner class to hold FAQ item data
     */
    public static class FaqItem {
        private final String question;
        private final String answer;

        public FaqItem(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() {
            return question;
        }

        public String getAnswer() {
            return answer;
        }
    }
}