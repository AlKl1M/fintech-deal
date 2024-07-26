package com.alkl1m.deal.web.controller;

import com.alkl1m.auditlogspringbootautoconfigure.annotation.AuditLog;
import com.alkl1m.deal.service.DealService;
import com.alkl1m.deal.web.payload.ChangeStatusPayload;
import com.alkl1m.deal.web.payload.DealDto;
import com.alkl1m.deal.web.payload.DealFiltersPayload;
import com.alkl1m.deal.web.payload.DealsDto;
import com.alkl1m.deal.web.payload.NewDealPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author alkl1m
 */
@Tag(name = "deal", description = "The Deal API")
@RestController
@AllArgsConstructor
@RequestMapping("/deal")
public class DealController {

    private final DealService dealService;

    /**
     * Сохранение/обновление сделки.
     *
     * @param payload информация о новой сделке.
     * @return DTO сделки.
     */
    @Operation(summary = "Сохранение/обновление сделки", tags = "deal")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Сохранил/обновил сделку",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = DealDto.class)))
                    })
    })
    @AuditLog
    @PutMapping("/save")
    public ResponseEntity<DealDto> saveOrUpdateDeal(@Validated @RequestBody NewDealPayload payload) {
        DealDto savedDeal = dealService.saveOrUpdate(payload);
        return ResponseEntity.ok(savedDeal);
    }

    /**
     * Получение сделки по id.
     *
     * @param id уникальный идентификатор сделки.
     * @return DTO сделки.
     */
    @Operation(summary = "Получение сделки по id", tags = "deal")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Получил сделку по id",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = DealDto.class)))
                    })
    })
    @AuditLog
    @GetMapping("/{id}")
    public ResponseEntity<DealDto> findById(@PathVariable UUID id) {
        DealDto contractorDtos = dealService.findById(id);
        return ResponseEntity.ok(contractorDtos);
    }

    /**
     * Изменение статуса сделки.
     *
     * @param payload информация о сделке и статусе.
     * @return ResponseEntity.
     */
    @Operation(summary = "Изменение статуса сделки", tags = "deal")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Изменил статус сделки"
            )
    })
    @AuditLog
    @PatchMapping("/change")
    public ResponseEntity<Void> changeStatus(@RequestBody ChangeStatusPayload payload) {
        dealService.changeStatus(payload);
        return ResponseEntity.ok().build();
    }

    /**
     * Получение сделки по заданным параметрам.
     *
     * @param payload информация о фильтрах.
     * @param page номер страницы.
     * @param size размер страницы.
     * @return DTO найденных сделок.
     */
    @Operation(summary = "Получение сделки по заданным параметрам", tags = "deal")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Нашел сделку по заданным параметрам",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = DealsDto.class)))
                    })
    })
    @AuditLog
    @PostMapping("/search")
    public ResponseEntity<DealsDto> search(
            @RequestBody DealFiltersPayload payload,
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @RequestParam(defaultValue = "10", required = false) Integer size,
            Principal principal
            ) {

        Authentication authentication = (Authentication) principal;

        Pageable paging = PageRequest.of(page, size);
        return ResponseEntity.ok(dealService.getDealsByParameters(payload, paging, authentication));
    }

}
