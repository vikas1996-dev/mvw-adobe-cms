package com.mvw.core.listeners;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = JobConsumer.class, property = {
        JobConsumer.PROPERTY_TOPICS + "=" + WorkflowTerminationEventHandler.WORKFLOW_TERMINATION_JOB_TOPIC
})
public class WorkflowTerminationJobConsumer implements JobConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowTerminationJobConsumer.class);
    private static final String SUBSERVICE = "mvwServiceWriter";
    private static final String WORKFLOW_METADATA_SUFFIX = "/data/metaData";
    private static final String WORKFLOW_PAYLOAD_SUFFIX = "/data/payload";
    private static final String DAM_METADATA_SUFFIX = "/jcr:content/metadata";

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    public JobResult process(Job job) {
        String instanceId = job.getProperty(WorkflowTerminationEventHandler.PROP_WORKFLOW_INSTANCE_ID, String.class);
        String payloadPath = job.getProperty(WorkflowTerminationEventHandler.PROP_PAYLOAD_PATH, String.class);
        if (StringUtils.isBlank(instanceId)) {
            LOG.warn("Workflow termination cleanup job missing workflow instance id.");
            return JobResult.CANCEL;
        }

        try (ResourceResolver resourceResolver = getServiceResolver()) {
            Resource workflowResource = resourceResolver.getResource(instanceId);
            if (workflowResource == null) {
                LOG.warn("No resource found for workflow instance ID: {}", instanceId);
                return JobResult.OK;
            }

            boolean hasChanges = false;
            Resource workflowMetaData = resourceResolver.getResource(instanceId + WORKFLOW_METADATA_SUFFIX);
            hasChanges |= removeScheduledPublishFields(workflowMetaData,
                    "workflow instance metadata");

            String assetPath = StringUtils.trimToNull(payloadPath);
            if (assetPath == null) {
                assetPath = readPayloadPathFromWorkflow(resourceResolver, instanceId);
            }
            if (StringUtils.isNotBlank(assetPath)) {
                Resource damMetaData = resourceResolver.getResource(assetPath + DAM_METADATA_SUFFIX);
                hasChanges |= removeScheduledPublishFields(damMetaData,
                        "dam asset metadata");
                hasChanges |= removeDamOnlyFields(damMetaData, "dam asset metadata");
            } else {
                LOG.warn("No payload path found for workflow instance {}", instanceId);
            }

            if (hasChanges) {
                resourceResolver.commit();
                LOG.info("Committed workflow termination cleanup changes for instanceId={}", instanceId);
            } else {
                LOG.info("No workflow termination cleanup changes needed for instanceId={}", instanceId);
            }
            return JobResult.OK;
        } catch (LoginException e) {
            LOG.error("Error in getting service resource resolver for workflow termination cleanup", e);
            return JobResult.FAILED;
        } catch (PersistenceException e) {
            LOG.error("Error in removing scheduled publish metadata properties", e);
            return JobResult.FAILED;
        }
    }

    private boolean removeScheduledPublishFields(Resource resource, String label) {
        if (resource == null) {
            LOG.warn("No resource found for {}", label);
            return false;
        }

        ModifiableValueMap valueMap = resource.adaptTo(ModifiableValueMap.class);
        if (valueMap == null) {
            LOG.warn("Resource {} could not adapt to ModifiableValueMap", resource.getPath());
            return false;
        }

        boolean removed = false;
        if (valueMap.containsKey("edtTime")) {
            valueMap.remove("edtTime");
            removed = true;
        }
        if (valueMap.containsKey("scheduled_publish_date")) {
            valueMap.remove("scheduled_publish_date");
            removed = true;
        }
        if (valueMap.containsKey("utc_time")) {
            valueMap.remove("utc_time");
            removed = true;
        }
        if (removed) {
            LOG.info("Removed scheduled publish metadata properties for {}: {}", label, resource.getPath());
        }
        return removed;
    }

    private boolean removeDamOnlyFields(Resource resource, String label) {
        if (resource == null) {
            LOG.warn("No resource found for {}", label);
            return false;
        }

        ModifiableValueMap valueMap = resource.adaptTo(ModifiableValueMap.class);
        if (valueMap == null) {
            LOG.warn("Resource {} could not adapt to ModifiableValueMap", resource.getPath());
            return false;
        }

        boolean removed = false;
        if (valueMap.containsKey("userId")) {
            valueMap.remove("userId");
            removed = true;
        }
        if (removed) {
            LOG.info("Removed DAM-only metadata properties for {}: {}", label, resource.getPath());
        }
        return removed;
    }

    private String readPayloadPathFromWorkflow(ResourceResolver resourceResolver, String instanceId) {
        Resource payloadResource = resourceResolver.getResource(instanceId + WORKFLOW_PAYLOAD_SUFFIX);
        if (payloadResource == null) {
            LOG.warn("No payload resource found for workflow instance {}", instanceId);
            return null;
        }
        return payloadResource.getValueMap().get("path", String.class);
    }

    private ResourceResolver getServiceResolver() throws LoginException {
        return resolverFactory.getServiceResourceResolver(
                Map.of(ResourceResolverFactory.SUBSERVICE, SUBSERVICE));
    }
}
