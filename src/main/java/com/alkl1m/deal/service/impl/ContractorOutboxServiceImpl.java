package com.alkl1m.deal.service.impl;

import com.alkl1m.deal.domain.entity.Contractor;
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

@Service
@RequiredArgsConstructor
public class ContractorOutboxServiceImpl implements ContractorOutboxService {

    private final ContractorOutboxRepository outboxRepository;
    private final EventBusService eventBusService;


    @Override
    public void save(ContractorOutbox contractorOutbox) {
        outboxRepository.save(contractorOutbox);
    }

    @Transactional
    public void publishNextBatchToEventBus(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdDate").ascending());
        Page<ContractorOutbox> batch = outboxRepository.findByStatus(ContractorOutboxStatus.CREATED, pageable);

        batch.forEach(contractorOutbox -> {
            ResponseEntity<Void> response = eventBusService.publishContractor(contractorOutbox);
            if (response.getStatusCode().is2xxSuccessful()) {
                contractorOutbox.setStatus(ContractorOutboxStatus.DONE);
            }
        });
    }


}
