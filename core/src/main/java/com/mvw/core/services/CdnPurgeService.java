package com.mvw.core.services;

import java.io.IOException;

/**
 * Builds CDN purge request metadata for resource-path and surrogate-key invalidation.
 */
public interface CdnPurgeService {

    CdnPurgeRequest buildPurgeRequest(String type, String domain, String resourcePath,
                                              String surrogateKey, String purgeTypeHeader);

    CdnPurgeResponse execute(CdnPurgeRequest purgeRequest) throws IOException, InterruptedException;
}
