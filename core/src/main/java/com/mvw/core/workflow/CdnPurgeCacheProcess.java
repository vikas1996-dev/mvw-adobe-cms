package com.mvw.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.mvw.core.constants.AppConstants;
import com.mvw.core.services.CdnPurgeRequest;
import com.mvw.core.services.CdnPurgeResponse;
import com.mvw.core.services.CdnPurgeService;
import com.mvw.core.services.impl.CdnPurgeConfigImpl;
import com.mvw.core.utils.WorkflowUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@Component(service = WorkflowProcess.class,
    property = {"process.label=CDN Purge Cache Process"})
public class CdnPurgeCacheProcess implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(CdnPurgeCacheProcess.class);

    @Reference
    private CdnPurgeConfigImpl cdnPurgeConfig;

    private static final String TYPE_RESOURCE = "resource";

    @Reference
    private CdnPurgeService cdnPurgeService;

    private String cdnPurgeDomain;

    @Activate
    protected void activate() {
        this.cdnPurgeDomain = cdnPurgeConfig.getCdnPurgeDomain();
    }

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) 
        throws WorkflowException {

        String payloadPath = StringUtils.EMPTY;
        WorkflowData workflowData = workItem.getWorkflowData();

        if ("JCR_PATH".equals(workflowData.getPayloadType())) {
            payloadPath = (String) workflowData.getPayload();
            payloadPath = payloadPath.equals(AppConstants.TMVC_BASE_CONTENT_PATH) ? AppConstants.FORWARD_SLASH : payloadPath.substring(AppConstants.TMVC_BASE_CONTENT_PATH.length());
        }

        Map<String, String> processArguments = WorkflowUtils.parseArgumentsToMap(metaDataMap.get("PROCESS_ARGS", ""));

        // Get values from workflow arguments (with defaults)
        String purgeTypeHeader = processArguments.getOrDefault("purgeTypeHeader", "X-AEM-Purge");
        String type = processArguments.getOrDefault("type", TYPE_RESOURCE);

        // Validate BEFORE making any calls for domain
        if (StringUtils.isBlank(payloadPath) || StringUtils.isBlank(cdnPurgeDomain)) {
            LOG.error("CDN purge skipped: domain is empty for payload: {}", payloadPath);
            throw new WorkflowException("CDN purge failed: 'path' or 'domain' is missing or empty in process arguments.");
        }

        try {
            CdnPurgeRequest purgeRequest = cdnPurgeService.buildPurgeRequest(type, cdnPurgeDomain, payloadPath, null, purgeTypeHeader);
            LOG.info("CDN purge request from workflow step triggered with headers: " + purgeRequest.getHeaders());
            CdnPurgeResponse purgeResponse = cdnPurgeService.execute(purgeRequest);

            // Log result for workflows
            if (purgeResponse.isSuccessful()) {
                LOG.info("CDN purge successful for path: {} | status: {}", payloadPath, purgeResponse.getStatusCode());
            } else {
                LOG.warn("CDN purge failed for path: {} | status: {} | response: {}", payloadPath, purgeResponse.getStatusCode(), purgeResponse.getResponseBody());
            }

        } catch (Exception e) {
            LOG.warn("Invalid CDN purge request for payload in the Workflow: {}", payloadPath, e);
        }
    }
}
