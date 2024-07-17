package com.alkl1m.deal.config;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SchedulerConfig {

    private final SchedulerProperties schedulerProperties;

    public SchedulerConfig(SchedulerProperties schedulerProperties) {
        this.schedulerProperties = schedulerProperties;
    }

    @Bean
    public Scheduler scheduler(List<Trigger> triggers, List<JobDetail> jobDetails, SchedulerFactoryBean factory) throws SchedulerException {
        factory.setWaitForJobsToCompleteOnShutdown(true);
        Scheduler scheduler = factory.getScheduler();
        revalidateJobs(jobDetails, scheduler);
        rescheduleTriggers(triggers, scheduler);
        scheduler.start();
        return scheduler;
    }

    private void rescheduleTriggers(List<Trigger> triggers, Scheduler scheduler) {
        triggers.forEach(trigger -> {
            try {
                if (!scheduler.checkExists(trigger.getKey())) {
                    scheduler.scheduleJob(trigger);
                } else {
                    scheduler.rescheduleJob(trigger.getKey(), trigger);
                }
            } catch (SchedulerException e) {
                // Handle exception
            }
        });
    }

    private void revalidateJobs(List<JobDetail> jobDetails, Scheduler scheduler) {
        List<JobKey> jobKeys = jobDetails.stream().map(JobDetail::getKey).collect(Collectors.toList());
        try {
            scheduler.getJobKeys(GroupMatcher.jobGroupEquals(schedulerProperties.getPermamentJobsGroupName())).forEach(jobKey -> {
                if (!jobKeys.contains(jobKey)) {
                    try {
                        scheduler.deleteJob(jobKey);
                    } catch (SchedulerException e) {
                        // Handle exception
                    }
                }
            });
        } catch (SchedulerException e) {
            // Handle exception
        }
    }
}