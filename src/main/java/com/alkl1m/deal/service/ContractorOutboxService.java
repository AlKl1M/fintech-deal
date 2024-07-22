package com.alkl1m.deal.service;

public interface ContractorOutboxService {

    void save(boolean isActiveToActive, String contractorId);

    void publishNextBatchToEventBus(int limit);
}
