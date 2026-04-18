package com.mvw.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.mvw.core.constants.WorkflowConstants;
import com.mvw.core.utils.WorkflowUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Workflow process step that sets arbitrary key-value pairs on the workflow
 * data metadata map. Used primarily to manage loop-control flags
 * (e.g. {@code continueLoop=true|false}) without requiring a dedicated
 * Java class for each flag.
 *
 * <p>PROCESS_ARGS format: {@code key1=value1, key2=value2}</p>
 */
@Component(
    service = WorkflowProcess.class,
    property = {"process.label=Set Workflow Metadata Process"}
)
public class SetMetadataProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(SetMetadataProcess.class);

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArgs)
            throws WorkflowException {

        MetaDataMap workflowDataMap = workItem.getWorkflowData().getMetaDataMap();
        String argsString = processArgs.get(WorkflowConstants.WF_PROCESS_ARGS, StringUtils.EMPTY);

        if (StringUtils.isBlank(argsString)) {
            log.warn("SetMetadataProcess invoked with no PROCESS_ARGS – skipping");
            return;
        }

        Map<String, String> kvPairs = WorkflowUtils.parseArgumentsToMap(argsString);
        for (Map.Entry<String, String> entry : kvPairs.entrySet()) {
            workflowDataMap.put(entry.getKey(), entry.getValue());
            log.info("Set workflow metadata: {} = {}", entry.getKey(), entry.getValue());
        }
    }

}
