package com.alkl1m.deal.config;

import com.alkl1m.deal.schedule.ContractorJob;
import lombok.RequiredArgsConstructor;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ContractorJobConfig {

    private final SchedulerProperties schedulerProperties;

    @Bean
    public JobDetail contractorJobDetail() {
        return JobBuilder.newJob(ContractorJob.class)
                .withIdentity("contractorJob", schedulerProperties.getPermamentJobsGroupName())
                .storeDurably()
                .requestRecovery(true)
                .build();
    }

    @Bean
    public Trigger showTimeTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(contractorJobDetail())
                .withIdentity("contractorJobTrigger", schedulerProperties
                        .getPermamentJobsGroupName())
                .withSchedule(CronScheduleBuilder.cronSchedule(schedulerProperties
                        .getContractorJobCron()))
                .build();
    }

}
