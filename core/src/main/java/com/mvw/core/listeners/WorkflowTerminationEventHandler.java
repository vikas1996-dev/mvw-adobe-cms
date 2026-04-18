package com.mvw.core.listeners;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.event.WorkflowEvent;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;

@Component(service = EventHandler.class, immediate = true, property = {
        EventConstants.EVENT_TOPIC + "=" + WorkflowEvent.EVENT_TOPIC
})
public class WorkflowTerminationEventHandler implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowTerminationEventHandler.class);
    static final String WORKFLOW_TERMINATION_JOB_TOPIC = "com/mvw/jobs/workflow/termination/cleanup";
    static final String PROP_WORKFLOW_INSTANCE_ID = "workflowInstanceId";
    static final String PROP_WORKFLOW_USER = "workflowUser";
    static final String PROP_PAYLOAD_PATH = "payloadPath";

    @Reference
    private JobManager jobManager;

    @Override
    public void handleEvent(Event event) {
        try {
            String eventType = (String) event.getProperty(WorkflowEvent.EVENT_TYPE);
            if (!WorkflowEvent.WORKFLOW_ABORTED_EVENT.equals(eventType)) {
                return;
            }

            String instanceId = (String) event.getProperty(WorkflowEvent.WORKFLOW_INSTANCE_ID);
            String modelName = (String) event.getProperty(WorkflowEvent.WORKFLOW_NAME);
            String modelNode = (String) event.getProperty(WorkflowEvent.WORKFLOW_NODE);
            String user = (String) event.getProperty(WorkflowEvent.USER);

            String payloadPath = (String) event.getProperty(PROP_PAYLOAD_PATH);

            LOG.info(
                    "Workflow TERMINATED (aborted). instanceId={}, modelName={}, modelNode={}, user={}",
                    instanceId, modelName, modelNode, user);

            WorkItem wi = (WorkItem) event.getProperty(WorkflowEvent.WORK_ITEM);
            if (wi != null) {
                LOG.warn("WorkItem id={}, node={}", wi.getId(), wi.getNode().getTitle());
            }
            Map<String, Object> jobProperties = new HashMap<>();
            jobProperties.put(PROP_WORKFLOW_INSTANCE_ID, instanceId);
            jobProperties.put(PROP_WORKFLOW_USER, user);
            jobProperties.put(PROP_PAYLOAD_PATH, payloadPath);
            jobProperties.put("ts", System.currentTimeMillis());
            jobManager.addJob(WORKFLOW_TERMINATION_JOB_TOPIC, jobProperties);
            LOG.info("Enqueued workflow termination cleanup job for instanceId={}", instanceId);
        } catch (Exception e) {
            LOG.error("Error while enqueuing workflow termination cleanup job", e);
        }

    }
}
