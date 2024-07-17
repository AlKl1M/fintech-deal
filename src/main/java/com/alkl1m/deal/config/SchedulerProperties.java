package com.alkl1m.deal.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scheduler")
@NoArgsConstructor
@Getter
@Setter
public class SchedulerProperties {

    private String permamentJobsGroupName = "PERMAMENT";
    private String contractorJobCron = "*/5 * * * * ?";

}
