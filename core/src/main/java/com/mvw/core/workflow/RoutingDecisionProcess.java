package com.mvw.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.mvw.core.constants.WorkflowConstants;
import com.mvw.core.utils.WorkflowUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

@Component(
    service = WorkflowProcess.class,
    property = {"process.label=Dynamic Branch Routing Decision (History Check)"}
)
public class RoutingDecisionProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(RoutingDecisionProcess.class);

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        MetaDataMap workflowDataMap = workItem.getWorkflowData().getMetaDataMap();
        boolean shouldRouteToThisBranch = false;
        String reviewerId = null;

        Map<String, String> argsMap = WorkflowUtils.parseArgumentsToMap(metaDataMap.get(WorkflowConstants.WF_PROCESS_ARGS, ""));
        String propertyNameToCheck = argsMap.get(WorkflowConstants.ARGS_KEY_REVIEWER);

        if (StringUtils.isNotBlank(propertyNameToCheck)) {
            List<HistoryItem> history = workflowSession.getHistory(workItem.getWorkflow());

            for (int i = history.size() - 1; i >= 0; i--) {
                HistoryItem historyItem = history.get(i);
                MetaDataMap stepMetadata = historyItem.getWorkItem().getMetaDataMap();
                MetaDataMap workflowDataMeta = historyItem.getWorkItem().getWorkflowData().getMetaDataMap();

                if (stepMetadata.containsKey(propertyNameToCheck)) {
                    reviewerId = stepMetadata.get(propertyNameToCheck, String.class);
                    if (StringUtils.isNotBlank(reviewerId)) {
                        break;
                    }
                }

                if (workflowDataMeta.containsKey(propertyNameToCheck)) {
                    String altReviewerId = workflowDataMeta.get(propertyNameToCheck, String.class);
                    if (StringUtils.isBlank(reviewerId) && StringUtils.isNotBlank(altReviewerId)) {
                        reviewerId = altReviewerId;
                        break;
                    }
                }
            }

            if (StringUtils.isNotBlank(reviewerId)) {
                log.info("Found reviewer '{}' from history with value: {}", propertyNameToCheck, reviewerId);
                shouldRouteToThisBranch = true;
                workflowDataMap.put(propertyNameToCheck, reviewerId);
            } else {
                log.info("Reviewer property '{}' not found in any history nodes.", propertyNameToCheck);
            }
        }

        workflowDataMap.put(WorkflowConstants.ARGS_KEY_REVIEWER, shouldRouteToThisBranch);
        log.info("Final routing decision '{}' stored in metadata key '{}'", shouldRouteToThisBranch, WorkflowConstants.ARGS_KEY_REVIEWER);
    }
}
