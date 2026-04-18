package com.mvw.core.workflow;

import com.mvw.core.constants.WorkflowConstants;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(property = { Constants.SERVICE_DESCRIPTION + "=Reviewer Chooser dynamically from the set of reviewers (self, qa, copyProofRead, business, legal)",
    "chooser.label" + "=Reviewer Routing Participant Chooser" })
public class DynamicReviewerChooserStep implements ParticipantStepChooser {

    private static final Logger logger = LoggerFactory.getLogger(DynamicReviewerChooserStep.class);

    @Override
    public String getParticipant(WorkItem workItem, WorkflowSession wfSession, MetaDataMap metaDataMap) {
        String participant;
        MetaDataMap workflowMetaDataMap = workItem.getWorkflowData().getMetaDataMap();

        String reviewStatus = workflowMetaDataMap.get(WorkflowConstants.WF_CURRENT_FLOW_REVIEW_STATUS, String.class);
        logger.info("Current Workflow Status: " + reviewStatus);

        switch (reviewStatus) {
            case WorkflowConstants.WF_STATUS_CATEGORY_TYPE_SELF:
                participant = workflowMetaDataMap.get(WorkflowConstants.PN_SELF_PEER_REVIEWER, String.class);
                break;

            case WorkflowConstants.WF_STATUS_CATEGORY_TYPE_QA:
                participant = workflowMetaDataMap.get(WorkflowConstants.PN_QA_REVIEWER, String.class);
                break;

            case WorkflowConstants.WF_STATUS_CATEGORY_TYPE_COPY_PROOF:
                participant = workflowMetaDataMap.get(WorkflowConstants.PN_COPYPROOFREAD_REVIEWER, String.class);
                break;

            case WorkflowConstants.WF_STATUS_CATEGORY_TYPE_BUSINESS:
                participant = workflowMetaDataMap.get(WorkflowConstants.PN_BUSINESS_REVIEWER, String.class);
                break;

            case WorkflowConstants.WF_STATUS_CATEGORY_TYPE_LEGAL:
                participant = workflowMetaDataMap.get(WorkflowConstants.PN_LEGAL_REVIEWER, String.class);
                break;

            default:
                participant = StringUtils.EMPTY;
                break;
        }

        return participant;
    }
}
