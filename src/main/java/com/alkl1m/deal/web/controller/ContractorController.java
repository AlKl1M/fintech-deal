package com.alkl1m.deal.web.controller;

import com.alkl1m.auditlogspringbootautoconfigure.annotation.AuditLog;
import com.alkl1m.deal.client.ContractorClient;
import com.alkl1m.deal.service.ContractorService;
import com.alkl1m.deal.web.payload.ContractorDto;
import com.alkl1m.deal.web.payload.DealDto;
import com.alkl1m.deal.web.payload.MainBorrowerMessage;
import com.alkl1m.deal.web.payload.NewContractorPayload;
import com.alkl1m.deal.web.payload.NewDealPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        ContractorDto savedContractor = contractorService.saveOrUpdate(payload);
        return ResponseEntity.ok(savedContractor);
    }

    @AuditLog
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteContractor(@RequestParam UUID contractorId) {
        MainBorrowerMessage message = contractorService.deleteContractorById(contractorId);
        client.mainBorrower(message.contractorId(), message.message());
        return ResponseEntity.ok().build();
    }

}
