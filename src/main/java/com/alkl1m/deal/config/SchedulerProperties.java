package com.alkl1m.deal.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Класс, хранящий параметры конфигурации планировщика.
 *
 * @author alkl1m
 */
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "scheduler")
public class SchedulerProperties {

    @Value("${deal.schedule.permamentJobsGroupName:PERMAMENT}")
    private String permamentJobsGroupName;
    @Value("${deal.schedule.contractorJobCron:*/5 * * * * ?}")
    private String contractorJobCron;

}
