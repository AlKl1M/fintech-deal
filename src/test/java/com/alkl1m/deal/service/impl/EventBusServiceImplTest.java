package com.alkl1m.deal.service.impl;

import com.alkl1m.deal.TestBeans;
import com.alkl1m.deal.client.ContractorClient;
import com.alkl1m.deal.domain.entity.ContractorOutbox;
import com.alkl1m.deal.domain.enums.ContractorOutboxStatus;
import com.alkl1m.deal.service.EventBusService;
import com.alkl1m.deal.web.payload.MainBorrowerRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = TestBeans.class)
public class EventBusServiceImplTest {

    @Autowired
    private EventBusService eventBusService;

    @MockBean
    private ContractorClient contractorClient;

    @Test
    public void testPublishContractor_withValidPayload_ReturnsValidResponse() {
        ContractorOutbox contractorOutbox = ContractorOutbox.builder()
                .id(100L)
                .createdDate(LocalDate.now())
                .main(true)
                .status(ContractorOutboxStatus.CREATED)
                .contractorId("TEST_CONTRACTOR")
                .build();

        MainBorrowerRequest request = new MainBorrowerRequest("TEST_CONTRACTOR", true);
        when(contractorClient.mainBorrower(request)).thenReturn(ResponseEntity.ok().build());

        ResponseEntity<Void> response = eventBusService.publishContractor(contractorOutbox);
        verify(contractorClient).mainBorrower(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

}