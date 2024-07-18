package com.alkl1m.deal.schedule;

import com.alkl1m.deal.service.ContractorOutboxService;
import lombok.RequiredArgsConstructor;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@RequiredArgsConstructor
public class ContractorJob extends QuartzJobBean {

    private final ContractorOutboxService outboxService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        outboxService.publishNextBatchToEventBus(10);
    }
}
