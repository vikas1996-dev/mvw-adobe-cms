package com.mvw.core.schedulers;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = EmailScheduler.Config.class)
public class EmailScheduler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(EmailScheduler.class);
    private static final String RUN_MODES_PROPERTY = "sling.run.modes";

    public static final String JOB_TOPIC = "com/mvw/jobs/email/send";

    @ObjectClassDefinition(name = "MVW - Email Job Scheduler", description = "Schedules a Sling Job to send emails. (Note: runs in server/JVM timezone)")
    public @interface Config {

        @AttributeDefinition(name = "Enabled")
        boolean enabled() default true;

        @AttributeDefinition(name = "Cron expression (used when test mode is OFF)", description = "Quartz cron. Default: 1:00 AM daily (server/JVM timezone).")
         String scheduler_expression() default "0 */15 * * * ?";

        //String scheduler_expression() default "0 0 2 * * ?";

        @AttributeDefinition(name = "Scheduler name")
        String scheduler_name() default "mvw.legal.email.scheduler";

        @AttributeDefinition(name = "Author only", description = "If true, scheduler will not run on publish even if configured.")
        boolean author_only() default true;
    }

    @Reference
    private Scheduler scheduler;

    @Reference
    private JobManager jobManager;

    private Config config;
    private Set<String> runModes = Collections.emptySet();

    @Activate
    protected void activate(Config config, BundleContext bundleContext) {
        configure(config, bundleContext);
        triggerImmediateRunOnActivate();
    }

    @Modified
    protected void modified(Config config, BundleContext bundleContext) {
        this.config = config;
        configure(config, bundleContext);
    }

    @Deactivate
    protected void deactivate() {
        if (config != null) {
            scheduler.unschedule(config.scheduler_name());
        }
    }

    private void configure(Config config, BundleContext bundleContext) {
        this.config = config;
        this.runModes = getRunModes(bundleContext);
        schedule();
    }

    private void triggerImmediateRunOnActivate() {
        if (config == null || !config.enabled()) {
            LOG.info("Skipping immediate EmailScheduler run on activate because scheduler is disabled.");
            return;
        }

        LOG.info("Triggering immediate EmailScheduler job on activate.");
        run();
    }

    private void schedule() {
        LOG.info("schedule() called.");

        if (config == null || !config.enabled()) {
            LOG.info("EmailScheduler disabled.");
            return;
        }

        scheduler.unschedule(config.scheduler_name());
        LOG.info("Unscheduled existing EmailScheduler (if any).");
        ScheduleOptions options = scheduler.EXPR(config.scheduler_expression());

        options.name(config.scheduler_name());
        options.canRunConcurrently(false);

        scheduler.schedule(this, options);

        LOG.info("Scheduled EmailScheduler with cron='{}' (server/JVM timezone)", config.scheduler_expression());
    }

    @Override
    public void run() {
        if (config.author_only() && !runModes.contains("author")) {
            LOG.debug("Skipping EmailScheduler on non-author. runModes={}", runModes);
            return;
        }

        try {
            Map<String, Object> props = new HashMap<>();
            props.put("source", "EmailScheduler");
            props.put("ts", System.currentTimeMillis());

            jobManager.addJob(JOB_TOPIC, props);

            LOG.info("Enqueued job topic={}", JOB_TOPIC);
        } catch (Exception e) {
            LOG.error("Failed to enqueue job {}", JOB_TOPIC, e);
        }
    }

    private Set<String> getRunModes(BundleContext bundleContext) {
        if (bundleContext == null) {
            return Collections.emptySet();
        }

        String configuredRunModes = bundleContext.getProperty(RUN_MODES_PROPERTY);
        if (configuredRunModes == null || configuredRunModes.trim().isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> parsedRunModes = new HashSet<>();
        for (String runMode : configuredRunModes.split(",")) {
            String trimmedRunMode = runMode.trim();
            if (!trimmedRunMode.isEmpty()) {
                parsedRunModes.add(trimmedRunMode);
            }
        }
        return parsedRunModes;
    }
}
