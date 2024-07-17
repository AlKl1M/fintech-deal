package com.alkl1m.deal.service.impl;

import com.alkl1m.deal.client.ContractorClient;
import com.alkl1m.deal.domain.entity.ContractorOutbox;
import com.alkl1m.deal.service.EventBusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventBusServiceImpl implements EventBusService {

    private final ContractorClient client;

    @Override
    public ResponseEntity<Void> publishContractor(ContractorOutbox contractorOutbox) {
        ResponseEntity<Void> voidResponseEntity = client.mainBorrower(contractorOutbox.getContractorId(), Boolean.valueOf(contractorOutbox.getPayload()));
        return voidResponseEntity;
    }
}
