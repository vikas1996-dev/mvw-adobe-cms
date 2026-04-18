package com.mvw.core.listeners;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = ResourceChangeListener.class, property = {
        ResourceChangeListener.PATHS + "=/var/workflow/instances",
        ResourceChangeListener.CHANGES + "=ADDED",
        ResourceChangeListener.CHANGES + "=CHANGED"
})
public class ManagePublicationAbsoluteTimeListener implements ResourceChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(ManagePublicationAbsoluteTimeListener.class);

    // Use a service user that has READ on /var/workflow and WRITE on
    // /content/dam/.../jcr:content/metadata
    private static final String SUBSERVICE = "mvwServiceWriter";

    // Write these properties on the DAM metadata node
    private static final String OUT_HUMAN_PROP = "absoluteTimeHuman";
    private static final String OUT_DATE_PROP = "absoluteTimeDate";

    // Output timezone for human-readable string
     private static final ZoneId DISPLAY_TZ = ZoneId.of("America/New_York");
    private static final ZoneId UTC_TZ = ZoneId.of("UTC");

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS z");
    private static final DateTimeFormatter UTC_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    @Reference
    private ResourceResolverFactory resolverFactory;

    private ResourceResolver rr;

    @Override
    public void onChange(List<ResourceChange> changes) {

        try {
            rr = getServiceResolver();
            Session session = rr.adaptTo(Session.class);
            if (session == null) {
                LOG.error("Unable to adapt service resolver to JCR Session for subservice={}", SUBSERVICE);
                return;
            }
            LOG.info("session permission:" + session.getUserID());
            LOG.info("Processing {} workflow changes", changes.size());
            LOG.info("session permission:"
                    + session.hasPermission("/var/workflow/instances", Session.ACTION_SET_PROPERTY));
            for (ResourceChange ch : changes) {
                try {
                    handleChange(ch.getPath());
                } catch (Exception e) {
                    LOG.error("Failed processing workflow change at path={}", ch.getPath(), e);
                }
            }
        } catch (LoginException | RepositoryException e) {
            LOG.error("Unable to obtain service ResourceResolver for subservice={}", SUBSERVICE, e);
        }
    }

    private void handleChange(String changedPath) throws PersistenceException {
        Resource instanceRes = resolveWorkflowInstanceResource(changedPath);
        LOG.info("instanceRes={}\n", instanceRes);
        if (instanceRes == null) {
            return;
        }
        String modelId = instanceRes.getValueMap().get("modelId", String.class);
        LOG.info("modelId={}\n", modelId);
        if (modelId == null || (!modelId.startsWith("/var/workflow/models/scheduled_tree_activation")
                && !modelId.startsWith("/var/workflow/models/scheduled_deactivation"))) {
            // Not our workflow model
            return;
        }
        String status = instanceRes.getValueMap().get("status", String.class);
        if (!"RUNNING".equalsIgnoreCase(status)) {
            LOG.info("Skipping workflow instance {} because status is {}", instanceRes.getPath(), status);
            return;
        }
        Resource dataRes = rr.getResource(instanceRes.getPath() + "/data");
        Resource metaRes = rr.getResource(instanceRes.getPath() + "/data/metaData");
        LOG.info("dataRes={} metaRes={}\n", dataRes, metaRes);

        if (dataRes == null || metaRes == null) {
            return;
        }
        LOG.info("metaRes details={}\n", metaRes.getPath() + " properties=" + metaRes.getValueMap());

        ValueMap metaVm = metaRes.getValueMap();
        Resource payloadRes = dataRes.getChild("payload");
        if (payloadRes == null) {
            LOG.debug("Payload node missing under workflow data for {}", instanceRes.getPath());
            return;
        }

        ValueMap payLoad = payloadRes.getValueMap();
        LOG.info("payload={} \n\nmetaData={}\n", payLoad, metaVm);

        // 1) Get payload path (what is being scheduled)
        String payloadPath = readString(payLoad, "path");
        if (StringUtils.isBlank(payloadPath)) {
            LOG.debug("Workflow payload path missing for {}", instanceRes.getPath());
            return;
        }

        if (!payloadPath.startsWith("/content/dam/legal/")) {
            LOG.info("payloadPath={} is not a legal DAM asset\n", payloadPath);
            return;
        }

        String metadataPath = payloadPath + "/jcr:content/metadata";
        String timeSharePlan = null;
        Resource damMeta = rr.getResource(metadataPath);
        Resource payloadAsset = rr.getResource(payloadPath);

        if (damMeta == null) {
            LOG.debug("DAM metadata node not found: {}\n", metadataPath);
            return;
        }
        String title = damMeta.getValueMap().get("dc:title", "");
        if (StringUtils.isBlank(title) && payloadAsset != null) {
            title = payloadAsset.getName();
        }

        if (payloadAsset != null && payloadAsset.getParent() != null) {
            Resource parentContent = payloadAsset.getParent().getChild("jcr:content");
            if (parentContent != null) {
                timeSharePlan = parentContent.getValueMap().get("jcr:title", String.class);
            }
        }

        // 2) Read absolute time from workflow metaData (try common keys)
        Long absMs = readEpochMillis(metaVm, "EpochMillis", "absoluteTime");
        String userId = readString(metaVm, "userId");

        if (absMs == null) {
            return;
        }

        // 3) Convert to human readable
        String human = FMT.format(Instant.ofEpochMilli(absMs).atZone(DISPLAY_TZ));
        String utcTime = UTC_FMT.format(Instant.ofEpochMilli(absMs).atZone(UTC_TZ));
        Calendar edtTime = GregorianCalendar.from(Instant.ofEpochMilli(absMs).atZone(DISPLAY_TZ));

        // 4) Write to DAM metadata
        ModifiableValueMap mvm = damMeta.adaptTo(ModifiableValueMap.class);
        if (mvm == null) {
            LOG.debug("DAM metadata node not modifiable: {}\n", metadataPath);
            return;
        }

        // Write string + date for reporting flexibility
        mvm.put("scheduled_publish_date", human.replace(":00.000", ""));
        mvm.put("utc_time", utcTime);
        mvm.put("edtTime", edtTime);
        if (StringUtils.isNotBlank(userId)) {
            mvm.put("userId", userId);
        }
        if (StringUtils.isNotBlank(timeSharePlan)) {
            mvm.put("timeSharePlan", timeSharePlan);
        }
        rr.commit();

        ModifiableValueMap metaDataValueMap = metaRes.adaptTo(ModifiableValueMap.class);
        if (metaDataValueMap == null) {
            LOG.debug("Workflow metadata node not modifiable: {}\n", metaRes.getPath());
            return;
        }

        // Write string + date for reporting flexibility
        metaDataValueMap.put("scheduled_publish_date", human.replace(":00.000", ""));
        metaDataValueMap.put("utc_time", utcTime);
        metaDataValueMap.put("edtTime", edtTime);
        if (StringUtils.isNotBlank(timeSharePlan)) {
            metaDataValueMap.put("timeSharePlan", timeSharePlan);
        }
        metaDataValueMap.put("title", title);

        rr.commit();

        LOG.info("Wrote {}={} and {}={} on {} (payload={})\n",
                OUT_HUMAN_PROP, human, OUT_DATE_PROP, absMs, metadataPath, payloadPath);
    }

    private Resource resolveWorkflowInstanceResource(String changedPath) {
        String candidatePath = changedPath;
        int dataIndex = changedPath.indexOf("/data");
        if (dataIndex > -1) {
            candidatePath = changedPath.substring(0, dataIndex);
        }

        while (StringUtils.startsWith(candidatePath, "/var/workflow/instances")) {
            Resource candidate = rr.getResource(candidatePath);
            if (candidate != null && StringUtils.isNotBlank(candidate.getValueMap().get("modelId", String.class))) {
                return candidate;
            }

            int lastSlash = candidatePath.lastIndexOf('/');
            if (lastSlash <= "/var/workflow/instances".length()) {
                break;
            }
            candidatePath = candidatePath.substring(0, lastSlash);
        }
        return null;
    }

    private ResourceResolver getServiceResolver() throws LoginException {
        return resolverFactory.getServiceResourceResolver(
                Map.of(ResourceResolverFactory.SUBSERVICE, SUBSERVICE));
    }

    private String readString(ValueMap vm, String... keys) {
        for (String k : keys) {
            String v = vm.get(k, String.class);
            if (StringUtils.isNotBlank(v)) {
                return v;
            }
        }
        return null;
    }

    private Long readEpochMillis(ValueMap vm, String... keys) {
        for (String k : keys) {
            Object v = vm.get(k);
            Long ms = toEpochMillis(v);
            if (ms != null) {
                return ms;
            }
        }
        return null;
    }

    private Long toEpochMillis(Object v) {
        if (v == null)
            return null;

        if (v instanceof Long)
            return (Long) v;
        if (v instanceof Integer)
            return ((Integer) v).longValue();

        if (v instanceof String) {
            String s = ((String) v).trim();
            if (s.matches("\\d+"))
                return Long.parseLong(s);
            return null;
        }

        if (v instanceof java.util.Calendar)
            return ((java.util.Calendar) v).getTimeInMillis();
        if (v instanceof java.util.Date)
            return ((java.util.Date) v).getTime();

        return null;
    }

}
