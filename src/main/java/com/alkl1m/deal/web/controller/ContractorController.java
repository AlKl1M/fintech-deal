package com.alkl1m.deal.web.controller;

import com.alkl1m.auditlogspringbootautoconfigure.annotation.AuditLog;
import com.alkl1m.authutilsspringbootautoconfigure.service.impl.UserDetailsImpl;
import com.alkl1m.deal.service.ContractorService;
import com.alkl1m.deal.web.payload.ContractorDto;
import com.alkl1m.deal.web.payload.NewContractorPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author alkl1m
 */
@Tag(name = "contractor", description = "The Deal API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/deal-contractor")
public class ContractorController {

    private final ContractorService contractorService;

    /**
     * Сохранение/обновление контрагента сделки.
     *
     * @param payload информация о новом контрагенте сделке.
     * @return dto нового контрагента.
     */
    @Operation(summary = "Сохранение/обновление контрагента сделки", tags = "contractor")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Сохранил/обновил контрагента сделки",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ContractorDto.class)))
                    })
    })
    @AuditLog
    @PutMapping("/save")
    @PreAuthorize("hasAnyAuthority('DEAL_SUPERUSER', 'SUPERUSER')")
    public ResponseEntity<ContractorDto> saveOrUpdateContractor(@Validated @RequestBody NewContractorPayload payload,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(contractorService.saveOrUpdate(payload, userDetails.getId()));
    }

    /**
     * Удаление контрагента сделки по ID.
     *
     * @param id уникальный идентификатор контрагента сделки.
     * @return ResponseEntity.
     */
    @Operation(summary = "Удаление контрагента сделки по ID", tags = "contractor")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Удалил контрагента по ID"
            )
    })
    @AuditLog
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('DEAL_SUPERUSER', 'SUPERUSER')")
    public ResponseEntity<Void> deleteContractor(@PathVariable UUID id) {
        contractorService.deleteContractorById(id);
        return ResponseEntity.ok().build();
    }

}
