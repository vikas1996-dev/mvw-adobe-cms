package com.mvw.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = WorkflowProcess.class,
        property = {"process.label=Sync Legal PDF JCR Title from DC Title"}
)
public class SyncLegalPdfTitleProcess implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(SyncLegalPdfTitleProcess.class);
    private static final String LEGAL_DAM_PATH = "/content/dam/legal/";
    private static final String METADATA_SUFFIX = "/jcr:content/metadata";
    private static final String ASSET_CONTENT_SUFFIX = "/jcr:content";
    private static final String DC_TITLE = "dc:title";
    private static final String JCR_TITLE = "jcr:title";

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) throws WorkflowException {
        LOG.info("asset name sync workflow started for work item: {}", workItem.getId());
        LOG.info("ASSET_CONTENT_SUFFIX: {}", ASSET_CONTENT_SUFFIX);
        ResourceResolver resolver = workflowSession.adaptTo(ResourceResolver.class);
        if (resolver == null) {
            throw new WorkflowException("Could not adapt workflow session to resource resolver");
        }

        String payloadPath = workItem.getWorkflowData().getPayload() != null
                ? workItem.getWorkflowData().getPayload().toString()
                : StringUtils.EMPTY;
        LOG.info("Workflow payload path: {}", payloadPath);

        String assetContentPath = normalizeAssetContentPath(payloadPath);
        if (StringUtils.isBlank(assetContentPath)) {
            LOG.info("Skipping workflow payload because it is not a legal PDF metadata/content path: {}", payloadPath);
            return;
        }

        Resource metadataResource = resolver.getResource(assetContentPath + "/metadata");
        if (metadataResource == null) {
            LOG.info("Skipping title sync because metadata node is missing for payload: {}", payloadPath);
            return;
        }

        ValueMap metadata = metadataResource.getValueMap();
        String dcTitle = StringUtils.trimToEmpty(metadata.get(DC_TITLE, String.class));
        LOG.info("dc:title for {} is {}", metadataResource.getPath(), dcTitle);
        if (StringUtils.isBlank(dcTitle)) {
            LOG.info("Skipping title sync because dc:title is blank for {}", metadataResource.getPath());
            return;
        }

        ModifiableValueMap metadataMap = metadataResource.adaptTo(ModifiableValueMap.class);
        if (metadataMap == null) {
            throw new WorkflowException("Could not adapt metadata resource to ModifiableValueMap: " + metadataResource.getPath());
        }

        String currentJcrTitle = StringUtils.trimToEmpty(metadataMap.get(JCR_TITLE, String.class));
        if (StringUtils.equals(currentJcrTitle, dcTitle)) {
            LOG.info("jcr:title already matches dc:title for {}", metadataResource.getPath());
            return;
        }

        metadataMap.put(JCR_TITLE, dcTitle);
        LOG.info("Updating jcr:title for {} from '{}' to '{}'", metadataResource.getPath(), currentJcrTitle, dcTitle);
        try {
            resolver.commit();
            LOG.info("Updated jcr:title for {} to match dc:title", metadataResource.getPath());
        } catch (PersistenceException e) {
            throw new WorkflowException("Failed to update jcr:title for " + metadataResource.getPath(), e);
        }
    }

    private String normalizeAssetContentPath(String payloadPath) {
        if (StringUtils.isBlank(payloadPath)) {
            return null;
        }

        String assetContentPath;
        LOG.info("Normalizing asset content path for payload: {}", payloadPath);
        if (StringUtils.endsWithIgnoreCase(payloadPath, ".pdf")) {
            assetContentPath = payloadPath + ASSET_CONTENT_SUFFIX;
        } else if (payloadPath.endsWith(METADATA_SUFFIX)) {
            assetContentPath = StringUtils.substringBeforeLast(payloadPath, "/metadata");
        } else if (payloadPath.endsWith(ASSET_CONTENT_SUFFIX)) {
            assetContentPath = payloadPath;
        } else {
            return null;
        }
        LOG.info("Normalized asset content path: {}", assetContentPath);

        String assetPath = StringUtils.substringBefore(assetContentPath, ASSET_CONTENT_SUFFIX);
        if (!StringUtils.startsWith(assetPath, LEGAL_DAM_PATH) || !StringUtils.endsWithIgnoreCase(assetPath, ".pdf")) {
            return null;
        }

        return assetContentPath;
    }
}
