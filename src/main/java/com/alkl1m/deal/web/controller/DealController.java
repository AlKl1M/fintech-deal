package com.alkl1m.deal.web.controller;

import com.alkl1m.auditlogspringbootautoconfigure.annotation.AuditLog;
import com.alkl1m.deal.client.ContractorClient;
import com.alkl1m.deal.domain.entity.Deal;
import com.alkl1m.deal.service.DealService;
import com.alkl1m.deal.web.payload.ChangeStatusPayload;
import com.alkl1m.deal.web.payload.ContractorDto;
import com.alkl1m.deal.web.payload.DealDto;
import com.alkl1m.deal.web.payload.DealFiltersPayload;
import com.alkl1m.deal.web.payload.MainBorrowerMessage;
import com.alkl1m.deal.web.payload.NewDealPayload;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/deal")
public class DealController {

    private final DealService dealService;
    private final ContractorClient client;

    @AuditLog
    @PostMapping("/save")
    public ResponseEntity<DealDto> saveOrUpdateDeal(@Validated @RequestBody NewDealPayload payload) {
        DealDto savedDeal = dealService.saveOrUpdate(payload);
        return ResponseEntity.ok(savedDeal);
    }

    @AuditLog
    @GetMapping("/{id}")
    public ResponseEntity<DealDto> findById(@PathVariable UUID id) {
        DealDto contractorDtos = dealService.findById(id);
        return ResponseEntity.ok(contractorDtos);
    }

    @AuditLog
    @PatchMapping("/change")
    public ResponseEntity<MainBorrowerMessage> findById(@RequestBody ChangeStatusPayload payload) {
        MainBorrowerMessage message = dealService.changeStatus(payload);
        client.mainBorrower(message.contractorId(), message.message());
        return ResponseEntity.ok(message);
    }

    @AuditLog
    @PostMapping("/search")
    public ResponseEntity<List<Deal>> search(@Validated @RequestBody DealFiltersPayload payload) {
        return ResponseEntity.ok(dealService.getDealsByParameters(payload));
    }

}
