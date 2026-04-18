package com.mvw.core.servlets;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/mvw/legalPdfTitleBackfill",
                "sling.servlet.methods=GET",
                "sling.servlet.methods=POST"
        }
)
public class LegalPdfTitleBackfillServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(LegalPdfTitleBackfillServlet.class);

    private static final String LEGAL_DAM_ROOT = "/content/dam/legal";
    private static final String DC_TITLE = "dc:title";
    private static final String JCR_TITLE = "jcr:title";
    private static final String PDF_EXTENSION = ".pdf";
    private static final int DEFAULT_BATCH_SIZE = 100;
    private static final int DEFAULT_MAX_RESULTS = Integer.MAX_VALUE;
    private static final int DEFAULT_SAMPLE_SIZE = 20;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        handleBackfill(request, response);
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        handleBackfill(request, response);
    }

    private void handleBackfill(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        ResourceResolver resolver = request.getResourceResolver();
        Resource legalRoot = resolver.getResource(LEGAL_DAM_ROOT);
        if (legalRoot == null) {
            response.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Legal DAM root not found\"}");
            return;
        }

        boolean dryRun = !"false".equalsIgnoreCase(StringUtils.trimToEmpty(request.getParameter("dryRun")));
        int batchSize = parsePositiveInt(request.getParameter("batchSize"), DEFAULT_BATCH_SIZE);
        int maxResults = parsePositiveInt(request.getParameter("maxResults"), DEFAULT_MAX_RESULTS);
        int sampleSize = parsePositiveInt(request.getParameter("sampleSize"), DEFAULT_SAMPLE_SIZE);

        BackfillResult result = new BackfillResult(dryRun, batchSize, maxResults, sampleSize);

        try {
            traverse(legalRoot, resolver, result);
            if (!dryRun && result.pendingCommitCount > 0) {
                resolver.commit();
            }
            response.getWriter().write(buildResponse(result));
        } catch (Exception e) {
            if (!dryRun) {
                resolver.revert();
            }
            LOG.error("Legal PDF title backfill failed", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void traverse(Resource resource, ResourceResolver resolver, BackfillResult result) throws PersistenceException {
        if (result.processedMatches >= result.maxResults) {
            return;
        }

        if (isPdfMetadataResource(resource)) {
            result.processedMatches++;
            processMetadataResource(resource, resolver, result);
            return;
        }

        Iterator<Resource> children = resource.listChildren();
        while (children.hasNext() && result.processedMatches < result.maxResults) {
            traverse(children.next(), resolver, result);
        }
    }

    private void processMetadataResource(Resource metadataResource, ResourceResolver resolver, BackfillResult result)
            throws PersistenceException {
        result.scanned++;

        ValueMap valueMap = metadataResource.getValueMap();
        String dcTitle = StringUtils.trimToEmpty(valueMap.get(DC_TITLE, String.class));
        String currentJcrTitle = StringUtils.trimToEmpty(valueMap.get(JCR_TITLE, String.class));

        if (StringUtils.isBlank(dcTitle)) {
            result.skippedBlankDcTitle++;
            return;
        }

        if (StringUtils.equals(dcTitle, currentJcrTitle)) {
            result.skippedAlreadySynced++;
            return;
        }

        result.candidates++;
        addSample(result.candidatePaths, metadataResource.getPath(), result.sampleSize);

        if (result.dryRun) {
            return;
        }

        ModifiableValueMap metadataMap = metadataResource.adaptTo(ModifiableValueMap.class);
        if (metadataMap == null) {
            result.skippedNonModifiable++;
            return;
        }

        metadataMap.put(JCR_TITLE, dcTitle);
        result.updated++;
        result.pendingCommitCount++;
        addSample(result.updatedPaths, metadataResource.getPath(), result.sampleSize);

        if (result.pendingCommitCount >= result.batchSize) {
            resolver.commit();
            result.pendingCommitCount = 0;
        }
    }

    private boolean isPdfMetadataResource(Resource resource) {
        if (resource == null || !StringUtils.equals(resource.getName(), "metadata")) {
            return false;
        }

        Resource assetContent = resource.getParent();
        Resource asset = assetContent != null ? assetContent.getParent() : null;
        return asset != null
                && StringUtils.startsWith(asset.getPath(), LEGAL_DAM_ROOT + "/")
                && StringUtils.endsWithIgnoreCase(asset.getPath(), PDF_EXTENSION);
    }

    private int parsePositiveInt(String value, int defaultValue) {
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }

        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void addSample(List<String> samplePaths, String path, int sampleSize) {
        if (samplePaths.size() < sampleSize) {
            samplePaths.add(path);
        }
    }

    private String buildResponse(BackfillResult result) {
        return "{"
                + "\"status\":\"success\","
                + "\"dryRun\":" + result.dryRun + ","
                + "\"batchSize\":" + result.batchSize + ","
                + "\"maxResults\":" + result.maxResults + ","
                + "\"scanned\":" + result.scanned + ","
                + "\"candidates\":" + result.candidates + ","
                + "\"updated\":" + result.updated + ","
                + "\"skippedBlankDcTitle\":" + result.skippedBlankDcTitle + ","
                + "\"skippedAlreadySynced\":" + result.skippedAlreadySynced + ","
                + "\"skippedNonModifiable\":" + result.skippedNonModifiable + ","
                + "\"candidatePaths\":" + toJsonArray(result.candidatePaths) + ","
                + "\"updatedPaths\":" + toJsonArray(result.updatedPaths)
                + "}";
    }

    private String toJsonArray(List<String> values) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append('"').append(escapeJson(values.get(i))).append('"');
        }
        builder.append(']');
        return builder.toString();
    }

    private String escapeJson(String value) {
        return StringUtils.defaultString(value)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    private static final class BackfillResult {
        private final boolean dryRun;
        private final int batchSize;
        private final int maxResults;
        private final int sampleSize;
        private final List<String> candidatePaths = new ArrayList<>();
        private final List<String> updatedPaths = new ArrayList<>();

        private int scanned;
        private int processedMatches;
        private int candidates;
        private int updated;
        private int skippedBlankDcTitle;
        private int skippedAlreadySynced;
        private int skippedNonModifiable;
        private int pendingCommitCount;

        private BackfillResult(boolean dryRun, int batchSize, int maxResults, int sampleSize) {
            this.dryRun = dryRun;
            this.batchSize = batchSize;
            this.maxResults = maxResults;
            this.sampleSize = sampleSize;
        }
    }
}
