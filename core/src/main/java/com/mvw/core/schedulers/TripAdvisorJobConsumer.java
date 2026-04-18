package com.mvw.core.schedulers;

import com.mvw.core.services.impl.SaveTripAdvisorDataJcr;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = JobConsumer.class, property = {
        JobConsumer.PROPERTY_TOPICS + "=com/mvw/jobs/tripadvisor/saveDataToJcr"
})
public class TripAdvisorJobConsumer implements JobConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(TripAdvisorJobConsumer.class);

    @Reference
    private SaveTripAdvisorDataJcr saveTripAdvisorDataJcr;

    @Override
    public JobResult process(Job job) {
        LOG.info("TripAdvisorJobConsumer started.");
        try {
            // This is the entry-point for the logic
            saveTripAdvisorDataJcr.saveDataToJcr();
            return JobResult.OK;
        } catch (Exception e) {
            LOG.error("EmailJobConsumer failed", e);
            return JobResult.FAILED;
        }
    }
}
