package com.mvw.core.workflow;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = ParticipantStepChooser.class, property = {
    "chooser.label=Reviewer Routing Participant Chooser (***DO NOT USE***)"
})
public class ReviewerRoutingParticipantChooser implements ParticipantStepChooser {

    private static final Logger log = LoggerFactory.getLogger(ReviewerRoutingParticipantChooser.class);

    public static final String ARG_REVIEWER_TYPE = "reviewerType";
    public static final String ARG_REVIEWER_KEY = "reviewerKey";

    @Override
    public String getParticipant(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) {
        String participant = null;

        String reviewerType = args.get(ARG_REVIEWER_TYPE, String.class);
        if (StringUtils.isBlank(reviewerType)) {
            reviewerType = args.get(ARG_REVIEWER_KEY, String.class);
        }

        MetaDataMap workflowMetadata = workItem.getWorkflowData().getMetaDataMap();
        MetaDataMap workflowInstanceMetadata = workItem.getWorkflow().getMetaDataMap();
        MetaDataMap workItemMetadata = workItem.getMetaDataMap();

        if (StringUtils.isNotBlank(reviewerType)) {
            participant = workflowMetadata.get(reviewerType, String.class);
            if (StringUtils.isBlank(participant)) {
                participant = workflowInstanceMetadata.get(reviewerType, String.class);
            }
            if (StringUtils.isBlank(participant)) {
                participant = workItemMetadata.get(reviewerType, String.class);
            }
        }

        log.debug("Resolved participant from metadata: {} for type: {}", participant, reviewerType);

        if (StringUtils.isBlank(participant)) {
            participant = args.get("PARTICIPANT", String.class);
            log.info("Participant not found in metadata. Falling back to default: {}", participant);
        }

        return participant;
    }
}
