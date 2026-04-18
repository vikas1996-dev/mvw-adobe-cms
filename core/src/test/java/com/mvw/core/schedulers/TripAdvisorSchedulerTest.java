package com.mvw.core.schedulers;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mvw.core.constants.AppConstants;
import java.util.HashSet;
import java.util.Set;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TripAdvisorSchedulerTest {

    @InjectMocks
    private TripAdvisorScheduler scheduler;

    @Mock
    private Scheduler slingScheduler;

    @Mock
    private SlingSettingsService slingSettingsService;

    @Mock
    private JobManager jobManager;

    @Mock
    private TripAdvisorScheduler.Config config;

    @Mock
    private ScheduleOptions scheduleOptions;

    @Mock
    private Job job;

    @Test
    void testActivate_OnAuthor_TriggersImmediateJob() {
        Set<String> runModes = new HashSet<>();
        runModes.add(AppConstants.RUNMODE_AUTHOR);
        when(config.scheduler_expression()).thenReturn("0 0 0 ? * WED");
        when(slingScheduler.EXPR(anyString())).thenReturn(scheduleOptions);
        when(slingSettingsService.getRunModes()).thenReturn(runModes);
        when(jobManager.addJob(anyString(), anyMap())).thenReturn(job);

        scheduler.activate(config);

        verify(slingScheduler).schedule(scheduler, scheduleOptions);
        verify(jobManager).addJob(eq(TripAdvisorScheduler.JOB_TOPIC), anyMap());
    }

    @Test
    void testActivate_OnNonAuthor_DoesNotTriggerImmediateJob() {
        when(config.scheduler_expression()).thenReturn("0 0 0 ? * WED");
        when(slingScheduler.EXPR(anyString())).thenReturn(scheduleOptions);
        when(slingSettingsService.getRunModes()).thenReturn(new HashSet<>());

        scheduler.activate(config);

        verify(slingScheduler).schedule(scheduler, scheduleOptions);
        verify(jobManager, never()).addJob(anyString(), anyMap());
    }
}
