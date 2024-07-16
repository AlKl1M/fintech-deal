package com.alkl1m.deal.web.controller;

import com.alkl1m.auditlogspringbootautoconfigure.annotation.AuditLog;
import com.alkl1m.deal.service.ContractorService;
import com.alkl1m.deal.web.payload.ContractorDto;
import com.alkl1m.deal.web.payload.DealDto;
import com.alkl1m.deal.web.payload.NewContractorPayload;
import com.alkl1m.deal.web.payload.NewDealPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deal-contractor")
public class ContractorController {

    private final ContractorService contractorService;

    @AuditLog
    @PutMapping("/save")
    public ResponseEntity<ContractorDto> saveOrUpdateContractor(@Validated @RequestBody NewContractorPayload payload) {
        ContractorDto savedContractor = contractorService.saveOrUpdate(payload);
        return ResponseEntity.ok(savedContractor);
    }

}
