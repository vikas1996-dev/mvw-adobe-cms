package com.mvw.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;

import lombok.Getter;

@Getter
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TemplateSiteConfigs {

    @SlingObject
    private Style currentStyle;

    @SlingObject
    private Resource resource;

    private String siteName;

    Logger logger = LoggerFactory.getLogger(TemplateSiteConfigs.class);

    @PostConstruct
    protected void init() {
        try {
            Page currentPage = resource.getResourceResolver().adaptTo(PageManager.class)
                    .getContainingPage(resource);

            if (currentPage != null && currentPage.getPath().indexOf("/conf") < 0) {
                // 1) First, check direct page properties
                siteName = currentPage.getProperties().get("siteName", String.class);

                // 2) If not found, check inherited page properties from parent pages
                if (siteName == null) {
                    InheritanceValueMap inheritanceValueMap = new HierarchyNodeInheritanceValueMap(
                            currentPage.getContentResource());
                    siteName = inheritanceValueMap.getInherited("siteName", String.class);
                }
            } else if (currentPage != null) {
                if (currentPage.getPath().indexOf("/conf") >= 0) {
                    resource = resource.getResourceResolver()
                            .getResource(currentPage.getPath()
                                    .replace("/structure", "/initial")).getChild("jcr:content");
                    siteName = resource.getValueMap().get("siteName", String.class);
                }
            }

            // 3) If still not found, fall back to template policy
            if (siteName == null && currentStyle != null) {
                siteName = currentStyle.get("siteName", String.class);
            }

            logger.info("Template site name is: {}", siteName);
        } catch (Exception e) {
            logger.error("Error retrieving inherited siteName", e);
        }
    }

    @Override
    public String toString() {
        return "TemplateSiteConfigs [siteName=" + siteName + "]";
    }
}
