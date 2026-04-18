package com.mvw.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.replication.*;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.Collections;
import java.util.Map;

/**
 * Custom Workflow Process to replicate content using a Service User.
 */
@Component(
    service = WorkflowProcess.class,
    property = {"process.label=Replicate Content to Preview/Publish (Service User)"}
)
public class ReplicateContentProcess implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(ReplicateContentProcess.class);
    
    // Using the same sub-service name from your Servlet for consistency
    private static final String SUB_SERVICE = "mvwWorkflowServiceUser";

    @Reference
    private Replicator replicator;

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) throws WorkflowException {
        
        // 1. Get the payload path
        String path = workItem.getWorkflowData().getPayload().toString();
        
        // 2. Get target tier from Arguments (default to 'publish')
        String agentId = args.get("PROCESS_ARGS", "publish").trim();

        // 3. Setup Auth Info for the Service User
        Map<String, Object> param = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SUB_SERVICE);

        // 4. Open Service Resolver and Replicate
        try (ResourceResolver serviceResolver = resolverFactory.getServiceResourceResolver(param)) {
            
            Session serviceSession = serviceResolver.adaptTo(Session.class);

            if (serviceSession != null) {
                LOG.info("Starting service user replication for path: {} to agent: {}", path, agentId);

                // Configure Replication Options to target a specific agent (publish or preview)
                ReplicationOptions options = new ReplicationOptions();
                options.setFilter(new AgentFilter() {
                    @Override
                    public boolean isIncluded(Agent agent) {
                        return agent.getId().equalsIgnoreCase(agentId);
                    }
                });

                // Execute Replication
                replicator.replicate(serviceSession, ReplicationActionType.ACTIVATE, path, options);
                
                LOG.info("Service user replication successful for: {}", path);
            } else {
                LOG.error("Could not obtain JCR Session from Service Resolver.");
                throw new WorkflowException("Session error");
            }

        } catch (LoginException e) {
            LOG.error("Login Exception: Could not get service resolver for {}", SUB_SERVICE, e);
            throw new WorkflowException("Service User Login failed", e);
        } catch (ReplicationException e) {
            LOG.error("Replication failed for path: {}", path, e);
            throw new WorkflowException("Replication failed", e);
        }
    }
}
