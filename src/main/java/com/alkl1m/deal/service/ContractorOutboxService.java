package com.alkl1m.deal.service;

import com.alkl1m.deal.domain.entity.ContractorOutbox;

public interface ContractorOutboxService {

    ContractorOutbox save(boolean isActiveToActive, String contractorId);

    void publishNextBatchToEventBus(int limit);
}
