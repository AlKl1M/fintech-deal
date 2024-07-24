package com.alkl1m.deal.web.payload;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO фильтра контрагента для сделок по роли.
 *
 * @param contractorId уникальный идентификатор контрагента.
 * @param name имя контрагента.
 * @param inn инн контрагента.
 * @author alkl1m
 */
public record ContractorFilterDto(

        @Schema(description = "Уникальный идентификатор контрагента")
        String contractorId,

        @Schema(description = "Имя контрагента")
        String name,

        @Schema(description = "ИНН контрагента")
        String inn
) {
}
