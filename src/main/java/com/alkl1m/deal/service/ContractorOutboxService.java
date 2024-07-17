package com.alkl1m.deal.service;

import com.alkl1m.deal.domain.entity.Contractor;
import com.alkl1m.deal.domain.entity.ContractorOutbox;

public interface ContractorOutboxService {

    void save(Contractor contractor);

    void publishNextBatchToEventBus();
}
