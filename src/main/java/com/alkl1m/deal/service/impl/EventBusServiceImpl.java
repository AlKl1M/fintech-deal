package com.alkl1m.deal.service.impl;

import com.alkl1m.deal.client.ContractorClient;
import com.alkl1m.deal.domain.entity.ContractorOutbox;
import com.alkl1m.deal.service.EventBusService;
import com.alkl1m.deal.web.payload.MainBorrowerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventBusServiceImpl implements EventBusService {

    private final ContractorClient client;

    @Override
    public ResponseEntity<Void> publishContractor(ContractorOutbox contractorOutbox) {
        MainBorrowerRequest request = new MainBorrowerRequest(contractorOutbox.getContractorId(), contractorOutbox.isMain());
        return client.mainBorrower(request);
    }
}
