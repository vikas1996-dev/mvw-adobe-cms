package com.mvw.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.Route;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.model.WorkflowNode;
import com.adobe.granite.workflow.model.WorkflowTransition;
import com.mvw.core.constants.WorkflowConstants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Conditionally performs a GOTO using {@code WorkflowSession.getBackRoutes()}.
 *
 * <p>When the condition IS met, finds a back route to the target node
 * (matching by title) and completes via that route.</p>
 *
 * <p>When the condition IS NOT met, completes via the first forward route.</p>
 *
 * <p>PROCESS_ARGS format:
 * {@code conditionKey=continueLoop, conditionValue=true, targetTitle=Content Authoring Team}</p>
 */
@Component(
    service = WorkflowProcess.class,
    property = {"process.label=Conditional Goto Process"}
)
public class ConditionalGotoProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(ConditionalGotoProcess.class);

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArgs)
            throws WorkflowException {

        MetaDataMap wfMeta = workItem.getWorkflowData().getMetaDataMap();
        String argsString = processArgs.get(WorkflowConstants.WF_PROCESS_ARGS, "");

        String conditionKey = "";
        String conditionValue = "";
        String targetTitle = "";

        for (String part : argsString.split(",")) {
            String[] kv = part.trim().split("=", 2);
            if (kv.length == 2) {
                switch (kv[0].trim()) {
                    case "conditionKey":   conditionKey   = kv[1].trim(); break;
                    case "conditionValue": conditionValue = kv[1].trim(); break;
                    case "targetTitle":    targetTitle    = kv[1].trim(); break;
                    default: break;
                }
            }
        }

        String actualValue = wfMeta.get(conditionKey, String.class);
        if (actualValue == null) actualValue = "";
        String stepTitle = workItem.getNode() != null ? workItem.getNode().getTitle() : "unknown";
        boolean conditionMet = conditionValue.equals(actualValue);

        List<Route> forwardRoutes = workflowSession.getRoutes(workItem, false);
        List<Route> backRoutes = workflowSession.getBackRoutes(workItem, true);

        if (conditionMet) {
            Route backRoute = findRouteByTargetTitle(backRoutes, targetTitle);

            if (backRoute != null) {
                log.info("ConditionalGotoProcess [{}]: condition MET. Going back to '{}' via back route '{}'.",
                        stepTitle, targetTitle, backRoute.getName());
                workflowSession.complete(workItem, backRoute);
                return;
            }

            log.warn("ConditionalGotoProcess [{}]: condition MET but no back route found for target '{}'. "
                    + "Total back routes: {}. Falling through to forward.", stepTitle, targetTitle, backRoutes.size());
        }

        if (!forwardRoutes.isEmpty()) {
            Route forwardRoute = forwardRoutes.get(0);
            log.info("ConditionalGotoProcess [{}]: Advancing forward (condition={}).",
                    stepTitle, conditionMet ? "met but no back route" : "not met");
            workflowSession.complete(workItem, forwardRoute);
        } else {
            log.warn("ConditionalGotoProcess [{}]: No routes found at all.", stepTitle);
        }
    }

    private Route findRouteByTargetTitle(List<Route> routes, String targetTitle) {
        if (targetTitle != null && !targetTitle.isEmpty()) {
            for (Route route : routes) {
                try {
                    for (WorkflowTransition dest : route.getDestinations()) {
                        WorkflowNode node = dest.getTo();
                        if (node != null && targetTitle.equals(node.getTitle())) {
                            return route;
                        }
                    }
                } catch (Exception e) {
                    log.debug("Error checking route destinations: {}", e.getMessage());
                }
            }
        }
        return routes.isEmpty() ? null : routes.get(0);
    }
}
