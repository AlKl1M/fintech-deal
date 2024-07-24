package com.alkl1m.deal.service.impl;

import com.alkl1m.deal.domain.entity.ContractorOutbox;
import com.alkl1m.deal.domain.enums.ContractorOutboxStatus;
import com.alkl1m.deal.repository.ContractorOutboxRepository;
import com.alkl1m.deal.service.EventBusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractorOutboxServiceImplTest {

    @Mock
    private ContractorOutboxRepository outboxRepository;

    @Mock
    private EventBusService eventBusService;

    @InjectMocks
    private ContractorOutboxServiceImpl contractorOutboxService;

    private int limit;
    private ContractorOutbox contractorOutbox1;
    private ContractorOutbox contractorOutbox2;
    private Page<ContractorOutbox> batch;

    @BeforeEach
    void setUp() {
        limit = 10;

        contractorOutbox1 = ContractorOutbox.builder()
                .id(100L)
                .createdDate(LocalDate.now())
                .main(true)
                .status(ContractorOutboxStatus.CREATED)
                .contractorId("TEST_CONTRACTOR1")
                .build();

        contractorOutbox2 = ContractorOutbox.builder()
                .id(101L)
                .createdDate(LocalDate.now())
                .main(true)
                .status(ContractorOutboxStatus.CREATED)
                .contractorId("TEST_CONTRACTOR2")
                .build();

        batch = new PageImpl<>(Arrays.asList(contractorOutbox1, contractorOutbox2));
    }

    @Test
    void testPublishNextBatchToEventBus_withTwoContractorOutboxes_returnsValidData() {
        when(outboxRepository.findByStatus(ContractorOutboxStatus.CREATED, PageRequest.of(0, limit, Sort.by("createdDate").ascending())))
                .thenReturn(batch);

        when(eventBusService.publishContractor(any())).thenReturn(ResponseEntity.ok().build());

        contractorOutboxService.publishNextBatchToEventBus(limit);

        verify(outboxRepository, times(1)).findByStatus(ContractorOutboxStatus.CREATED, PageRequest.of(0, limit, Sort.by("createdDate").ascending()));
        verify(eventBusService, times(2)).publishContractor(any());
        assertEquals(ContractorOutboxStatus.DONE, contractorOutbox1.getStatus());
        assertEquals(ContractorOutboxStatus.DONE, contractorOutbox2.getStatus());
    }

    @Test
    void testPublishNextBatchToEventBus_withFailureOnSecondContractorOutbox_returnsSameStatus() {
        when(outboxRepository.findByStatus(ContractorOutboxStatus.CREATED, PageRequest.of(0, limit, Sort.by("createdDate").ascending())))
                .thenReturn(batch);

        when(eventBusService.publishContractor(any()))
                .thenReturn(ResponseEntity.ok().build())
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        contractorOutboxService.publishNextBatchToEventBus(limit);

        verify(outboxRepository, times(1)).findByStatus(ContractorOutboxStatus.CREATED, PageRequest.of(0, limit, Sort.by("createdDate").ascending()));
        verify(eventBusService, times(2)).publishContractor(any());

        assertEquals(ContractorOutboxStatus.DONE, contractorOutbox1.getStatus());
        assertEquals(ContractorOutboxStatus.CREATED, contractorOutbox2.getStatus());
    }

}