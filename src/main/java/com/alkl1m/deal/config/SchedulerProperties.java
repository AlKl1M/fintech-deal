package com.alkl1m.deal.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    private String permamentJobsGroupName = "PERMAMENT";
    private String contractorJobCron = "*/5 * * * * ?";

}
