package com.mvw.core.services;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable request description for a CDN PURGE call.
 */
public final class CdnPurgeRequest {

    private final String method;
    private final String url;
    private final Map<String, String> headers;

    public CdnPurgeRequest(String method, String url, Map<String, String> headers) {
        this.method = method;
        this.url = url;
        this.headers = Collections.unmodifiableMap(new LinkedHashMap<>(headers));
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
