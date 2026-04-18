package com.mvw.core.services.impl;

import com.mvw.core.constants.AppConstants;
import com.mvw.core.services.CdnPurgeRequest;
import com.mvw.core.services.CdnPurgeResponse;
import com.mvw.core.services.CdnPurgeService;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Component(service = CdnPurgeService.class, immediate = true)
public class CdnPurgeServiceImpl implements CdnPurgeService {

    private static final Logger LOG = LoggerFactory.getLogger(CdnPurgeServiceImpl.class);

    @Reference
    private CdnPurgeConfigImpl cdnPurgeConfig;

    private static final String PURGE_METHOD = "PURGE";
    private static final String PURGE_TYPE_VALUE = "hard";
    private static final String PURGE_KEY_HEADER = "X-AEM-Purge-Key";
    private static final String SURROGATE_KEY_HEADER = "Surrogate-Key";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

    private final HttpClient httpClient;
    private volatile String cdnPurgeKey1;
    private volatile String cdnPurgeKey2;

    private static final String TYPE_RESOURCE = "resource";
    private static final String TYPE_SURROGATE = "surrogate";

    public CdnPurgeServiceImpl() {
        this(HttpClient.newHttpClient());
    }

    CdnPurgeServiceImpl(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Activate
    protected void activate() {
        this.cdnPurgeKey1 = StringUtils.trimToEmpty(cdnPurgeConfig.getCdnPurgeKey1());
        this.cdnPurgeKey2 = StringUtils.trimToEmpty(cdnPurgeConfig.getCdnPurgeKey2());
    }

    // Added this setter ONLY for unit testing
    public void setConfigService(CdnPurgeConfigImpl configService) {
        this.cdnPurgeConfig = configService;
    }

    public CdnPurgeRequest buildResourcePathPurgeRequest(String resourcePath, String domain,
                                                         String purgeTypeHeader) {
        String normalizedDomain = normalizeDomain(domain);
        String normalizedPath = normalizePath(resourcePath);
        String normalizedPurgeTypeHeader = normalizePurgeTypeHeader(purgeTypeHeader);

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(normalizedPurgeTypeHeader, PURGE_TYPE_VALUE);
        addConfiguredPurgeKey(headers);
        LOG.info("CDN PurgeRequest headers object: " + headers);
        String pageUrlToBePurged = "https://" + normalizedDomain + normalizedPath + AppConstants.DOT_HTML_EXTENSION;
        LOG.info("CDN PurgeRequest PageUrl: " + pageUrlToBePurged);

        return new CdnPurgeRequest(PURGE_METHOD, pageUrlToBePurged, headers);
    }

    @Override
    public CdnPurgeRequest buildPurgeRequest(String type, String domain, String resourcePath,
                                              String surrogateKey, String purgeTypeHeader) {
        if (TYPE_RESOURCE.equals(type)) {
            return buildResourcePathPurgeRequest(resourcePath, domain, purgeTypeHeader);
        }

        if (TYPE_SURROGATE.equals(type)) {
            return buildSurrogateKeyPurgeRequest(domain, surrogateKey, purgeTypeHeader);
        }

        throw new IllegalArgumentException("type must be resource or surrogate");
    }

    public CdnPurgeRequest buildSurrogateKeyPurgeRequest(String domain, String surrogateKey,
                                                         String purgeTypeHeader) {
        String normalizedDomain = normalizeDomain(domain);
        String normalizedSurrogateKey = StringUtils.trimToEmpty(surrogateKey);
        String normalizedPurgeTypeHeader = normalizePurgeTypeHeader(purgeTypeHeader);

        if (StringUtils.isBlank(normalizedSurrogateKey)) {
            throw new IllegalArgumentException("Surrogate key must not be blank");
        }

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(normalizedPurgeTypeHeader, PURGE_TYPE_VALUE);
        addConfiguredPurgeKey(headers);
        headers.put(SURROGATE_KEY_HEADER, normalizedSurrogateKey);

        return new CdnPurgeRequest(PURGE_METHOD, "https://" + normalizedDomain + "/", headers);
    }

    @Override
    public CdnPurgeResponse execute(CdnPurgeRequest purgeRequest) throws IOException, InterruptedException {
        validateRequest(purgeRequest);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(purgeRequest.getUrl()))
                .timeout(REQUEST_TIMEOUT)
                .method(purgeRequest.getMethod(), HttpRequest.BodyPublishers.noBody());

        for (Map.Entry<String, String> header : purgeRequest.getHeaders().entrySet()) {
            requestBuilder.header(header.getKey(), header.getValue());
        }

        HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        return new CdnPurgeResponse(response.statusCode(), response.body());
    }

    private String normalizeDomain(String domain) {
        String normalizedDomain = StringUtils.trimToEmpty(domain);
        normalizedDomain = removePrefixIgnoreCase(normalizedDomain, "https://");
        normalizedDomain = removePrefixIgnoreCase(normalizedDomain, "http://");
        normalizedDomain = StringUtils.stripEnd(normalizedDomain, "/");

        if (StringUtils.isBlank(normalizedDomain)) {
            throw new IllegalArgumentException("Domain must not be blank");
        }

        return normalizedDomain;
    }

    private String normalizePath(String resourcePath) {
        String normalizedPath = StringUtils.trimToEmpty(resourcePath);

        if (StringUtils.isBlank(normalizedPath)) {
            throw new IllegalArgumentException("Resource path must not be blank");
        }

        if (!normalizedPath.startsWith("/")) {
            normalizedPath = "/" + normalizedPath;
        }

        return normalizedPath;
    }

    private String normalizePurgeTypeHeader(String purgeTypeHeader) {
        String normalizedPurgeTypeHeader = StringUtils.trimToEmpty(purgeTypeHeader);

        if (StringUtils.isBlank(normalizedPurgeTypeHeader)) {
            throw new IllegalArgumentException("Purge type header must not be blank");
        }

        return normalizedPurgeTypeHeader;
    }

    private void validateRequest(CdnPurgeRequest purgeRequest) {
        if (purgeRequest == null) {
            throw new IllegalArgumentException("Purge request must not be null");
        }

        if (StringUtils.isBlank(purgeRequest.getMethod())) {
            throw new IllegalArgumentException("Purge request method must not be blank");
        }

        if (StringUtils.isBlank(purgeRequest.getUrl())) {
            throw new IllegalArgumentException("Purge request URL must not be blank");
        }
    }

    private String removePrefixIgnoreCase(String value, String prefix) {
        if (StringUtils.isBlank(value) || StringUtils.isBlank(prefix)) {
            return value;
        }

        return value.regionMatches(true, 0, prefix, 0, prefix.length())
                ? value.substring(prefix.length())
                : value;
    }

    private void addConfiguredPurgeKey(Map<String, String> headers) {
        LOG.info("CDN Purge key1: {} & key2: {}", cdnPurgeKey1, cdnPurgeKey2);
        String purgeKey = firstNonBlank(cdnPurgeKey1, cdnPurgeKey2);
        if (StringUtils.isNotBlank(purgeKey)) {
            headers.put(PURGE_KEY_HEADER, purgeKey);
        }
    }

    private String firstNonBlank(String first, String second) {
        if (StringUtils.isNotBlank(first)) {
            return first;
        }
        if (StringUtils.isNotBlank(second)) {
            return second;
        }
        return null;
    }
}
