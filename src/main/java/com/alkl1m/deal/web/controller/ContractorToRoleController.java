package com.alkl1m.deal.web.controller;

import com.alkl1m.auditlogspringbootautoconfigure.annotation.AuditLog;
import com.alkl1m.deal.service.ContractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    @PostMapping("/add")
    public ResponseEntity<Void> saveOrUpdateContractor(@RequestParam UUID contractorId, @RequestParam String roleId) {
        contractorService.addRoleToContractor(contractorId, roleId);
        return ResponseEntity.ok().build();
    }

    @AuditLog
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteRoleForContractor(@RequestParam UUID contractorId, @RequestParam String roleId) {
        contractorService.deactivateRoleForContractor(contractorId, roleId);
        return ResponseEntity.ok().build();
    }

}
