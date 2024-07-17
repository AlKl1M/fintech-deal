package com.alkl1m.deal.service;

import com.alkl1m.deal.web.payload.ContractorDto;
import com.alkl1m.deal.web.payload.MainBorrowerMessage;
import com.alkl1m.deal.web.payload.NewContractorPayload;

import java.util.UUID;

public interface ContractorService {

    ContractorDto saveOrUpdate(NewContractorPayload payload);

    void deleteContractorById(UUID id);

    void addRoleToContractor(UUID contractorId, String roleId);

    void deactivateRoleForContractor(UUID contracorId, String roleId);

}
