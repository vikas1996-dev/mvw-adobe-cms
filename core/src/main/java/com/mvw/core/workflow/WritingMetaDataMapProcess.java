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
import java.util.*;

@Component(
    service = WorkflowProcess.class,
    property = {"process.label=Writing MetaData Properties for Reviewers from History List"}
)
public class WritingMetaDataMapProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(WritingMetaDataMapProcess.class);

    List<String> keyOfReviewerSet = List.of("selfPeerReviewer", "qaReviewer", "copyProofreadReviewer",
                            "businessReviewer", "legalReviewer");

    @Override
    public void execute(WorkItem workItem, WorkflowSession wfSession, MetaDataMap metadataMap) throws WorkflowException{
        MetaDataMap metadatamap = workItem.getWorkflow().getWorkflowData().getMetaDataMap();

        boolean flowHasBeenSentBack = Optional.ofNullable(metadatamap)
                                            .map(metaMap -> metaMap.get("continueLoop", String.class))
                                            .map(Boolean::parseBoolean)
                                            .orElse(false);

        log.info("Value for continue loop=flowHasBeenSentBack:: " + flowHasBeenSentBack);

        if (flowHasBeenSentBack) {
            keyOfReviewerSet.forEach(item -> {
                if (metadatamap.containsKey(item)) {
                    metadatamap.put(item, StringUtils.EMPTY);
                }
            });
        }

        List<HistoryItem> historyList = wfSession.getHistory(workItem.getWorkflow());
        int listSize = historyList.size();
        HistoryItem lastItem = historyList.get(listSize - 1);
        for(Map.Entry<String, Object> entrySet : lastItem.getWorkItem().getMetaDataMap().entrySet()) {

            if (keyOfReviewerSet.contains(entrySet.getKey()) && StringUtils.isNotEmpty(entrySet.getValue().toString())) {
                log.debug("Setting workflow metadata property:{}, value:{}", entrySet.getKey(), entrySet.getValue());
                metadatamap.put(entrySet.getKey(), entrySet.getValue());
            }
        }
    }
}
