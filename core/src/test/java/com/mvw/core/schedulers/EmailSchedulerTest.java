package com.mvw.core.schedulers;

import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.osgi.framework.BundleContext;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmailSchedulerTest {

    @InjectMocks
    private EmailScheduler scheduler;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private Scheduler slingScheduler;

    @Mock
    private JobManager jobManager;

    @Mock
    private EmailScheduler.Config config;

    @Mock
    private ScheduleOptions scheduleOptions;

    @Mock
    private Job job;

    @BeforeEach
    void setUp() {
        when(config.enabled()).thenReturn(true);
        when(config.scheduler_expression()).thenReturn("0 0 1 * * ?");
        when(config.scheduler_name()).thenReturn("mvw.legal.email.scheduler");
        when(config.author_only()).thenReturn(true);
        when(slingScheduler.EXPR(anyString())).thenReturn(scheduleOptions);
        when(bundleContext.getProperty("sling.run.modes")).thenReturn("author");
    }

    @Test
    void testActivate_Enabled() {
        when(jobManager.addJob(anyString(), anyMap())).thenReturn(job);

        scheduler.activate(config, bundleContext);

        verify(slingScheduler).unschedule("mvw.legal.email.scheduler");
        verify(slingScheduler).schedule(scheduler, scheduleOptions);
        verify(jobManager).addJob(eq("com/mvw/jobs/email/send"), anyMap());
    }

    @Test
    void testActivate_Disabled() {
        when(config.enabled()).thenReturn(false);

        scheduler.activate(config, bundleContext);

        verify(slingScheduler, never()).unschedule(anyString());
        verify(slingScheduler, never()).schedule(any(Runnable.class), any(ScheduleOptions.class));
        verify(jobManager, never()).addJob(anyString(), anyMap());
    }

    @Test
    void testModified() {
        scheduler.activate(config, bundleContext);
        reset(slingScheduler);
        when(slingScheduler.EXPR(anyString())).thenReturn(scheduleOptions);

        scheduler.modified(config, bundleContext);

        verify(slingScheduler).unschedule("mvw.legal.email.scheduler");
        verify(slingScheduler).schedule(scheduler, scheduleOptions);
    }

    @Test
    void testDeactivate() {
        scheduler.activate(config, bundleContext);

        scheduler.deactivate();

        verify(slingScheduler, times(2)).unschedule("mvw.legal.email.scheduler");
    }

    @Test
    void testRun_OnAuthor() {
        when(bundleContext.getProperty("sling.run.modes")).thenReturn("author");
        when(jobManager.addJob(anyString(), anyMap())).thenReturn(job);

        scheduler.activate(config, bundleContext);
        clearInvocations(jobManager);
        scheduler.run();

        verify(jobManager).addJob(eq("com/mvw/jobs/email/send"), anyMap());
    }

    @Test
    void testRun_OnPublish_AuthorOnly() {
        when(bundleContext.getProperty("sling.run.modes")).thenReturn("publish");

        scheduler.activate(config, bundleContext);
        scheduler.run();

        verify(jobManager, never()).addJob(anyString(), anyMap());
    }

    @Test
    void testRun_OnPublish_NotAuthorOnly() {
        when(config.author_only()).thenReturn(false);
        when(bundleContext.getProperty("sling.run.modes")).thenReturn("publish");
        when(jobManager.addJob(anyString(), anyMap())).thenReturn(job);

        scheduler.activate(config, bundleContext);
        clearInvocations(jobManager);
        scheduler.run();

        verify(jobManager).addJob(eq("com/mvw/jobs/email/send"), anyMap());
    }

    @Test
    void testRun_JobManagerException() {
        when(bundleContext.getProperty("sling.run.modes")).thenReturn("author");
        when(jobManager.addJob(anyString(), anyMap())).thenThrow(new RuntimeException("Job error"));

        scheduler.activate(config, bundleContext);
        scheduler.run();

        // Exception should be caught and logged
    }
}
