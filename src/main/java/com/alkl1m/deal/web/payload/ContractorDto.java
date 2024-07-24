package com.alkl1m.deal.web.payload;

import com.alkl1m.deal.domain.entity.Contractor;
import com.alkl1m.deal.domain.entity.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;
import java.util.UUID;

/**
 * DTO контрагента.
 *
 * @param id уникальный идентификатор контрагента в сделке.
 * @param contractorId идентификатор контрагента.
 * @param name имя контрагента.
 * @param main является ли основным.
 * @param roles set ролей.
 * @author alkl1m
 */
public record ContractorDto(

        @JsonProperty("id")
        @Schema(description = "Уникальный идентификатор контрагента сделки")
        UUID id,

        @JsonProperty("contractor_id")
        @Schema(description = "Уникальный идентификатор контрагента")
        String contractorId,

        @JsonProperty("name")
        @Schema(description = "Имя контрагента")
        String name,

        @JsonProperty("main")
        @Schema(description = "Является ли контрагент основным")
        boolean main,

        @JsonProperty("roles")
        @Schema(description = "Список ролей контрагента")
        Set<Role> roles
) {

    public static ContractorDto from (Contractor contractor) {
        return new ContractorDto(
                contractor.getId(),
                contractor.getContractorId(),
                contractor.getName(),
                contractor.isMain(),
                contractor.getRoles()
        );
    }

}
