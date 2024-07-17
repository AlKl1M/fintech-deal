package com.alkl1m.deal.web.controller;

import com.alkl1m.auditlogspringbootautoconfigure.annotation.AuditLog;
import com.alkl1m.deal.client.ContractorClient;
import com.alkl1m.deal.service.ContractorService;
import com.alkl1m.deal.web.payload.ContractorDto;
import com.alkl1m.deal.web.payload.MainBorrowerMessage;
import com.alkl1m.deal.web.payload.NewContractorPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deal-contractor")
public class ContractorController {

    private final ContractorService contractorService;
    private final ContractorClient client;

    @AuditLog
    @PutMapping("/save")
    public ResponseEntity<ContractorDto> saveOrUpdateContractor(@Validated @RequestBody NewContractorPayload payload) {
        return ResponseEntity.ok(contractorService.saveOrUpdate(payload));
    }

    @AuditLog
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteContractor(@PathVariable UUID id) {
        MainBorrowerMessage message = contractorService.deleteContractorById(id);
        client.mainBorrower(message.contractorId(), message.message());
        return ResponseEntity.ok().build();
    }

}
