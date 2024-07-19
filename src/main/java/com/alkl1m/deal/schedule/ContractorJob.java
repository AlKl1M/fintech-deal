package com.alkl1m.deal.schedule;

import com.alkl1m.deal.service.ContractorOutboxService;
import lombok.RequiredArgsConstructor;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Класс ContractorJob представляет собой задачу Quartz
 * для публикации данных контрактных исполнителей в шину событий.
 * @author alkl1m
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@RequiredArgsConstructor
public class ContractorJob extends QuartzJobBean {

    private final ContractorOutboxService outboxService;

    /**
     * Метод executeInternal выполняет задачу по публикации следующей порции данных контрагентов в шину событий.
     *
     * @param context контекст выполнения задачи
     * @throws JobExecutionException если произошла ошибка при выполнении задачи
     */
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        outboxService.publishNextBatchToEventBus(10);
    }
}
