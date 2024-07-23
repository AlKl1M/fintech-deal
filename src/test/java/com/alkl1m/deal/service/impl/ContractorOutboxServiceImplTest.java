package com.alkl1m.deal.service.impl;

import com.alkl1m.deal.domain.entity.ContractorOutbox;
import com.alkl1m.deal.domain.enums.ContractorOutboxStatus;
import com.alkl1m.deal.repository.ContractorOutboxRepository;
import com.alkl1m.deal.service.EventBusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractorOutboxServiceImplTest {

    @Mock
    private ContractorOutboxRepository outboxRepository;

    @Mock
    private EventBusService eventBusService;

    @InjectMocks
    private ContractorOutboxServiceImpl contractorOutboxService;

    @Test
    void testPublishNextBatchToEventBus_withTwoContractorOutboxes_returnsValidData() {
        int limit = 10;
        ContractorOutbox contractorOutbox1 = ContractorOutbox.builder()
                .id(100L)
                .createdDate(new Date())
                .main(true)
                .status(ContractorOutboxStatus.CREATED)
                .contractorId("TEST_CONTRACTOR1")
                .build();
        ContractorOutbox contractorOutbox2 = ContractorOutbox.builder()
                .id(100L)
                .createdDate(new Date())
                .main(true)
                .status(ContractorOutboxStatus.CREATED)
                .contractorId("TEST_CONTRACTOR2")
                .build();
        Page<ContractorOutbox> batch = new PageImpl<>(Arrays.asList(contractorOutbox1, contractorOutbox2));

        when(outboxRepository.findByStatus(ContractorOutboxStatus.CREATED, PageRequest.of(0, limit, Sort.by("createdDate").ascending())))
                .thenReturn(batch);

        when(eventBusService.publishContractor(any())).thenReturn(ResponseEntity.ok().build());

        contractorOutboxService.publishNextBatchToEventBus(limit);

        verify(outboxRepository, times(1)).findByStatus(ContractorOutboxStatus.CREATED, PageRequest.of(0, limit, Sort.by("createdDate").ascending()));
        verify(eventBusService, times(2)).publishContractor(any());
        assert(contractorOutbox1.getStatus() == ContractorOutboxStatus.DONE);
        assert(contractorOutbox2.getStatus() == ContractorOutboxStatus.DONE);
    }

}