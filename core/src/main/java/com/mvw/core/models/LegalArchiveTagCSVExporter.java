package com.mvw.core.models;

import java.util.Arrays;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.acs.commons.reports.api.ReportCellCSVExporter;
import com.day.cq.search.result.Hit;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.tagging.TagConstants;

@Model(adaptables = Resource.class)
public class LegalArchiveTagCSVExporter implements ReportCellCSVExporter {

    private static final Logger log = LoggerFactory.getLogger(LegalArchiveTagCSVExporter.class);

    private static final String ARCHIVE_TAG_ID = "legal:archive"; // expected tag.getTagID()

    @Override
    public String getValue(Object result) {

        Resource asset = toResource(result);
        if (asset == null) {
            log.warn("CSV Exporter: unable to adapt result to Resource. Type={}", (result == null ? "null" : result.getClass()));
            return "No";
        }

        Resource metadata = asset.getChild("jcr:content/metadata");
        if (metadata == null) {
            return "No";
        }

        String[] tagIds = metadata.getValueMap().get(TagConstants.PN_TAGS, String[].class);
        if (tagIds == null || tagIds.length == 0) {
            return "No";
        }

        // Fast path: sometimes stored tags already match your ID
        if (Arrays.asList(tagIds).contains(ARCHIVE_TAG_ID)) {
            return "Yes";
        }

        TagManager tagManager = asset.getResourceResolver().adaptTo(TagManager.class);
        if (tagManager == null) {
            // If TagManager isn't available, fall back to raw string compare if needed
            return "No";
        }

        for (String tagId : tagIds) {
            Tag tag = tagManager.resolve(tagId);
            if (tag != null && ARCHIVE_TAG_ID.equalsIgnoreCase(tag.getTagID())) {
                return "Yes";
            }
        }

        return "No";
    }

    private Resource toResource(Object result) {
        try {
            if (result instanceof Resource) {
                return (Resource) result;
            }
            if (result instanceof Hit) {
                return ((Hit) result).getResource();
            }
        } catch (RepositoryException e) {
            log.warn("CSV Exporter: error resolving resource for result type={}", result != null ? result.getClass() : "null", e);
        }
        return null;
    }
}
