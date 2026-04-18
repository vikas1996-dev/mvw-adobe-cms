package com.mvw.core.services.impl;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.mvw.core.services.LocDataService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import javax.jcr.Session;
import javax.servlet.http.Cookie;

@Component(service = LocDataService.class)
public class LocDataServiceImpl implements LocDataService {

    private static final Logger LOG = LoggerFactory.getLogger(LocDataServiceImpl.class);

    private static final String CF_PATH = "/content/dam/tmvcs/loc/default-loc-data";
    private static final String COOKIE_NAME = "loc";

    @Override
    public Map<String, String> processLocData(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        Map<String, String> locInfo = new HashMap<>();
        String locParam = sanitizeCookieValue(request.getParameter("loc"));
        ResourceResolver resolver = request.getResourceResolver();

        if (locParam != null && !locParam.isEmpty()) {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("path", "/content/dam/tmvcs");
            queryMap.put("type", "dam:Asset");
            queryMap.put("property", "jcr:content/data/master/locCode");
            queryMap.put("property.value", locParam);
            queryMap.put("property.operation", "equalsIgnoreCase");

            QueryBuilder queryBuilder = resolver.adaptTo(QueryBuilder.class);
            Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), resolver.adaptTo(Session.class));
            SearchResult result = query.getResult();

            if (!result.getHits().isEmpty()) {
                try {
                    Resource cfResource = result.getHits().get(0).getResource();
                    ContentFragment cf = cfResource.adaptTo(ContentFragment.class);
                    locInfo.put("loc", locParam);
                    locInfo.put("phoneNumber", cf.getElement("phoneNumber").getContent());
                } catch (Exception e) {
                    LOG.error("Error retrieving dynamic CF", e);
                }
            } else {
                handleDefaultLoc(request, response, locInfo);
            }

            // Set or update loc cookie
            setOrUpdateCookie(request, response, COOKIE_NAME, locParam);

        } else {
            handleDefaultLoc(request, response, locInfo);
        }

        request.setAttribute("loc", locInfo.get("loc"));
        request.setAttribute("phoneNumber", locInfo.get("phoneNumber"));

        return locInfo;
    }

    private void handleDefaultLoc(SlingHttpServletRequest request, SlingHttpServletResponse response, Map<String, String> locInfo) {
        LOG.info("Retrieving default location from {}", CF_PATH);
        String locCode = getElementValue(request, "locCode");
        locInfo.put("loc", locCode);
        locInfo.put("phoneNumber", getElementValue(request, "phoneNumber"));

        if (locCode != null && response != null) {
            String safeValue = sanitizeCookieValue(locCode);
            Cookie cookie = new Cookie(COOKIE_NAME, safeValue);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // Uncomment when HTTPS is enabled
            response.addCookie(cookie);
            LOG.info("Set session cookie '{}' with sanitized value: {}", COOKIE_NAME, safeValue);
        }
    }

    private void setOrUpdateCookie(SlingHttpServletRequest request, SlingHttpServletResponse response, String name, String value) {
        String safeValue = sanitizeCookieValue(value);
        Cookie locCookie = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (name.equals(cookie.getName())) {
                    locCookie = cookie;
                    break;
                }
            }
        }

        if (locCookie != null) {
            locCookie.setValue(safeValue);
            locCookie.setPath("/");
            locCookie.setHttpOnly(true);
            locCookie.setSecure(true);
            response.addCookie(locCookie);
            LOG.info("Updated existing '{}' cookie with sanitized value: {}", name, safeValue);
        } else {
            Cookie newCookie = new Cookie(name, safeValue);
            newCookie.setPath("/");
            newCookie.setHttpOnly(true);
            newCookie.setSecure(true);
            response.addCookie(newCookie);
            LOG.info("Created new '{}' cookie with sanitized value: {}", name, safeValue);
        }
    }

    private String getElementValue(SlingHttpServletRequest request, String elementName) {
        ResourceResolver resolver = request.getResourceResolver();
        Resource cfResource = resolver.getResource(CF_PATH);

        if (cfResource != null) {
            ContentFragment cf = cfResource.adaptTo(ContentFragment.class);
            if (cf != null) {
                ContentElement element = cf.getElement(elementName);
                if (element != null && element.getContent() != null) {
                    return element.getContent();
                } else {
                    LOG.warn("Element '{}' not found in Content Fragment at {}", elementName, CF_PATH);
                }
            } else {
                LOG.error("Resource at {} could not be adapted to ContentFragment.", CF_PATH);
            }
        } else {
            LOG.error("Default Content Fragment not found at path: {}", CF_PATH);
        }
        return elementName;
    }

    /**
     * CRLF + unsafe character protection for cookies
     */
    private String sanitizeCookieValue(String value) {
        if (value == null) return "";
        // Remove CRLF characters
        value = value.replaceAll("[\\r\\n]", "");
        // Replace unsafe characters with underscore
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}