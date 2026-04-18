package com.mvw.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component(
    service = WorkflowProcess.class,
    property = {"process.label=Copy Reviewer Action to Workflow Data"}
)
public class CopyReviewerActionProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(CopyReviewerActionProcess.class);
    private static final String REVIEWER_ACTION_KEY = "reviewerAction";
    private static final String COMMENT_KEY = "comment";

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArgs) throws WorkflowException {
        MetaDataMap workflowDataMap = workItem.getWorkflowData().getMetaDataMap();

        try {
            List<HistoryItem> history = workflowSession.getHistory(workItem.getWorkflow());
            String reviewerAction = null;
            String comment = null;

            for (int i = history.size() - 1; i >= 0; i--) {
                HistoryItem historyItem = history.get(i);
                MetaDataMap stepMetadata = historyItem.getWorkItem().getMetaDataMap();

                if (stepMetadata.containsKey(REVIEWER_ACTION_KEY)) {
                    reviewerAction = stepMetadata.get(REVIEWER_ACTION_KEY, String.class);
                    if (stepMetadata.containsKey(COMMENT_KEY)) {
                        comment = stepMetadata.get(COMMENT_KEY, String.class);
                    }
                    break;
                }
            }

            if (StringUtils.isNotBlank(reviewerAction)) {
                workflowDataMap.put(REVIEWER_ACTION_KEY, reviewerAction);
                if (StringUtils.isNotBlank(comment)) {
                    workflowDataMap.put(COMMENT_KEY, comment);
                }
            } else {
                log.warn("reviewerAction not found in any history item - OR split may incorrectly route to Approve branch");
            }
        } catch (Exception e) {
            log.error("Failed to copy reviewer action", e);
            throw new WorkflowException("Failed to copy reviewer action", e);
        }
    }
}
