package com.alkl1m.deal.service.impl;

import com.alkl1m.deal.domain.entity.Contractor;
import com.alkl1m.deal.domain.entity.ContractorOutbox;
import com.alkl1m.deal.domain.enums.ContractorOutboxStatus;
import com.alkl1m.deal.repository.ContractorOutboxRepository;
import com.alkl1m.deal.service.ContractorOutboxService;
import com.alkl1m.deal.service.EventBusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractorOutboxServiceImpl implements ContractorOutboxService {

    private final ContractorOutboxRepository outboxRepository;
    private final EventBusService eventBusService;


    @Override
    public void save(Contractor contractor) {
        ContractorOutbox contractorOutbox = ContractorOutbox.builder()
                .createdDate(new Date())
                .payload(String.valueOf(contractor.isMain()))
                .idempotentKey(UUID.randomUUID().toString())
                .status(ContractorOutboxStatus.CREATED)
                .contractorId(contractor.getContractorId())
                .build();
        outboxRepository.save(contractorOutbox);
    }

    @Transactional
    public void publishNextBatchToEventBus() {
        List<ContractorOutbox> batch = outboxRepository.findByStatus(ContractorOutboxStatus.CREATED);
        batch.forEach(contractorOutbox -> {
            ResponseEntity<Void> response = eventBusService.publishContractor(contractorOutbox);
            if (response.getStatusCode().is2xxSuccessful()) {
                contractorOutbox.setStatus(ContractorOutboxStatus.DONE);
            }
        });
    }


}
