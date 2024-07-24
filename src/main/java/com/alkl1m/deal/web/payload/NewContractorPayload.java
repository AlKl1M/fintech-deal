package com.alkl1m.deal.web.payload;

import com.alkl1m.deal.domain.entity.Contractor;
import com.alkl1m.deal.domain.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * Payload для создания нового контрагента сделки.
 *
 * @param id уникальный идентификатор контрагента сделки.
 * @param dealId уникальный идентификатор сделки.
 * @param contractorId уникальный идентификатор контрагента.
 * @param name имя контрагента.
 * @param inn инн контрагента.
 * @param main статус основного контрагента.
 * @param roles список ролей контрагента
 * @author alkl1m
 */
public record NewContractorPayload(

        @Schema(description = "Уникальный идентификатор контрагента сделки")
        UUID id,

        @Schema(description = "Уникальный идентификатор сделки")
        UUID dealId,

        @Schema(description = "Уникальный идентификатор контрагента")
        String contractorId,

        @Schema(description = "Имя контрагента")
        String name,

        @Schema(description = "ИНН контрагента")
        String inn,

        @Schema(description = "Статус основного контрагента")
        boolean main,

        @Schema(description = "Список ролей контрагента")
        Set<Role> roles
) {

    public static Contractor toContractor(NewContractorPayload payload, String userId) {
        return Contractor.builder()
                .id(payload.id)
                .dealId(payload.dealId)
                .contractorId(payload.contractorId)
                .name(payload.name)
                .inn(payload.inn)
                .main(payload.main)
                .roles(payload.roles)
                .createDate(LocalDate.now())
                .createUserId(userId)
                .isActive(true)
                .build();
    }

}
