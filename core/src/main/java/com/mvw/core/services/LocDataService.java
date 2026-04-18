package com.mvw.core.services;

import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

public interface LocDataService {
    Map<String, String> processLocData(SlingHttpServletRequest request, SlingHttpServletResponse response);
}
