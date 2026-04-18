package com.mvw.core.models;
 
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.commons.Externalizer;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
import javax.annotation.PostConstruct;
 
@Model(adaptables = SlingHttpServletRequest.class)
public class PageMetadata {
 
    private static final Logger LOGGER = LoggerFactory.getLogger(PageMetadata.class);
    private static final String FEATURED_IMAGE_PROPERTY = "cq:featuredimage/fileReference";
    private static final String TWITTERCARD_IMAGE_PROPERTY = "cq:twitterimage/fileReference";
 
    @Self
    private SlingHttpServletRequest request;
 
    @OSGiService
    private Externalizer externalizer;
 
    private String ogImageAbsolutePath;
    private String twitterImageAbsolutePath;
    private String currentPageAbsolutePath;
 
    @PostConstruct
    protected void init() {
        ResourceResolver resolver = request.getResourceResolver();
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        if (pageManager != null) {
            Page page = pageManager.getContainingPage(request.getResource());
            if (page != null) {
                // Get the absolute path for the current page
                this.currentPageAbsolutePath = externalizePath(resolver, page.getPath());
 
                // Get the absolute path for the featured image
                String ogRelativePath = page.getProperties().get(FEATURED_IMAGE_PROPERTY, String.class);
                if (ogRelativePath != null) {
                    this.ogImageAbsolutePath = externalizePath(resolver, ogRelativePath);
                }
 
                // Get the absolute path for the Twitter card image
                String twitterRelativePath = page.getProperties().get(TWITTERCARD_IMAGE_PROPERTY, String.class);
                if (twitterRelativePath != null) {
                    this.twitterImageAbsolutePath = externalizePath(resolver, twitterRelativePath);
                }
            }
        }
    }
 
    /**
     * Helper method to externalize a path.
     *
     * @param resolver The resource resolver.
     * @param relativePath The path to externalize.
     * @return The absolute externalized URL, or null if it fails.
     */
    private String externalizePath(ResourceResolver resolver, String relativePath) {
        try {
            return externalizer.externalLink(resolver, "tmvcs", relativePath);
        } catch (Exception e) {
            LOGGER.error("Could not externalize path: {}", relativePath, e);
            return null;
        }
    }
 
    public String getOgImageAbsolutePath() {
        return ogImageAbsolutePath;
    }
 
    public String getTwitterImageAbsolutePath() {
        return twitterImageAbsolutePath;
    }
 
    public String getCurrentPageAbsolutePath() {
        return currentPageAbsolutePath;
    }
}
