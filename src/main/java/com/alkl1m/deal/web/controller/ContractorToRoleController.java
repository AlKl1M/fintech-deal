package com.alkl1m.deal.web.controller;

import com.alkl1m.auditlogspringbootautoconfigure.annotation.AuditLog;
import com.alkl1m.deal.service.ContractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contractor-to-role")
public class ContractorToRoleController {

    private final ContractorService contractorService;

    @AuditLog
    @PostMapping("/add/{id}")
    public ResponseEntity<Void> saveOrUpdateContractor(@PathVariable UUID id, @RequestParam String roleId) {
        contractorService.addRoleToContractor(id, roleId);
        return ResponseEntity.ok().build();
    }

    @AuditLog
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRoleForContractor(@PathVariable UUID id, @RequestParam String roleId) {
        contractorService.deactivateRoleForContractor(id, roleId);
        return ResponseEntity.ok().build();
    }

}
