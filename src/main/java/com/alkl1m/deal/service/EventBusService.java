package com.alkl1m.deal.service;

import com.alkl1m.deal.domain.entity.ContractorOutbox;
import org.springframework.stereotype.Service;

@Service
public interface EventBusService {

    void publishContractor(ContractorOutbox contractorOutbox);

}
