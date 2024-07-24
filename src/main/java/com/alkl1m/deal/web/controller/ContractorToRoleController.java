package com.alkl1m.deal.web.controller;

import com.alkl1m.auditlogspringbootautoconfigure.annotation.AuditLog;
import com.alkl1m.deal.service.ContractorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author alkl1m
 */
@Tag(name = "Role", description = "The Deal API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/contractor-to-role")
public class ContractorToRoleController {

    private final ContractorService contractorService;

    /**
     * Добавление роли контрагенту сделки.
     *
     * @param id уникальный идентификатор контрагента сделки.
     * @param roleId роль контрагента.
     * @return ResponseEntity.
     */
    @Operation(summary = "Добавление роли контрагенту сделки", tags = "Role")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Добавил роль контрагенту сделки"
            )
    })
    @AuditLog
    @PostMapping("/add/{id}")
    public ResponseEntity<Void> addRoleToContractor(@PathVariable UUID id, @RequestParam String roleId) {
        contractorService.addRoleToContractor(id, roleId);
        return ResponseEntity.ok().build();
    }

    /**
     * Удаление роли у контрагента сделки.
     *
     * @param id уникальный идентификатор контрагента сделки.
     * @param roleId роль контрагента сделки.
     * @return ResponseEntity.
     */
    @Operation(summary = "Удаление роли у контрагента сделки", tags = "Role")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Убрал роль контрагенту сделки"
            )
    })
    @AuditLog
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRoleForContractor(@PathVariable UUID id, @RequestParam String roleId) {
        contractorService.deactivateRoleForContractor(id, roleId);
        return ResponseEntity.ok().build();
    }

}
