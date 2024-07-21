package com.alkl1m.deal.service.impl;

import com.alkl1m.deal.domain.entity.ContractorOutbox;
import com.alkl1m.deal.domain.enums.ContractorOutboxStatus;
import com.alkl1m.deal.repository.ContractorOutboxRepository;
import com.alkl1m.deal.service.ContractorOutboxService;
import com.alkl1m.deal.service.EventBusService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация сервиса для работы с outbox контрактных исполнителей.
 *
 * @author alkl1m
 */
@Service
@RequiredArgsConstructor
public class ContractorOutboxServiceImpl implements ContractorOutboxService {

    private final ContractorOutboxRepository outboxRepository;
    private final EventBusService eventBusService;

    /**
     * Метод save сохраняет данные контрактного исполнителя в outbox.
     *
     * @param contractorOutbox данные контрактного исполнителя для сохранения
     */
    @Override
    public void save(ContractorOutbox contractorOutbox) {
        outboxRepository.save(contractorOutbox);
    }

    /**
     * Метод publishNextBatchToEventBus публикует следующую порцию данных контрактных исполнителей в шину событий.
     *
     * @param limit количество записей для публикации
     */
    @Transactional
    public void publishNextBatchToEventBus(int limit) {
        Page<ContractorOutbox> batch = outboxRepository.findByStatus(
                ContractorOutboxStatus.CREATED,
                PageRequest.of(0, limit, Sort.by("createdDate").ascending())
        );

        batch.forEach(contractorOutbox -> {
            if (eventBusService.publishContractor(contractorOutbox).getStatusCode().is2xxSuccessful()) {
                contractorOutbox.setStatus(ContractorOutboxStatus.DONE);
            }
        });
    }


}
