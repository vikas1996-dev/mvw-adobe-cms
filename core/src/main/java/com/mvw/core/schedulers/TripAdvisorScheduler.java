package com.mvw.core.schedulers;

import com.mvw.core.constants.AppConstants;
import org.apache.sling.commons.scheduler.*;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.*;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = TripAdvisorScheduler.Config.class)
public class TripAdvisorScheduler implements Runnable {

    @Reference
    private Scheduler scheduler;

    @Reference
    private SlingSettingsService slingSettingsService;

    @Reference
    private JobManager jobManager;

    private String schedulerJobName = "TripAdvisorSchedulerJob";

    public static final String JOB_TOPIC = "com/mvw/jobs/tripadvisor/saveDataToJcr";

    @ObjectClassDefinition(name = "TripAdvisor AEM Scheduler")
    public @interface Config {
        @AttributeDefinition(
            name = "Cron Expression",
            description = "Default: runs every Wednesday, specifically at the top of the hour."
        )
        String scheduler_expression() default "0 0 0 ? * WED";
    }

    private String cronExpression;

    @Activate
    protected void activate(Config config) {
        this.cronExpression = config.scheduler_expression();
        ScheduleOptions options = scheduler.EXPR(cronExpression);
        options.name(schedulerJobName);
        options.canRunConcurrently(false);
        scheduler.schedule(this, options);

        logger.info("TripAdvisorScheduler scheduled with expression: {}", cronExpression);
        logger.info("Triggering immediate TripAdvisor job on activate.");
        run();
    }

    @Deactivate
    protected void deactivate() {
        scheduler.unschedule(schedulerJobName);
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run() {
        logger.info("Start: TripAdvisorScheduler RUN method()");
        if (!slingSettingsService.getRunModes().contains(AppConstants.RUNMODE_AUTHOR)) {
            return;
        }

        try {
            jobManager.addJob(JOB_TOPIC, new HashMap<>());
            logger.info("Enqueued job topic={}", JOB_TOPIC);
        } catch (Exception e) {
            logger.error("Failed to enqueue job {}", JOB_TOPIC, e);
        }
    }
}
