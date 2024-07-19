package com.alkl1m.deal.web.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Request для отправки статуса основного заемщика.
 *
 * @param contractorId уникальный идентификатор контрагента.
 * @param main статус основного заемщика.
 * @author alkl1m
 */
public record MainBorrowerRequest(

        @NotNull(message = "{deal.main_borrower.contractor_id_is_null}")
        @Schema(description = "Уникальный идентификатор сделки", nullable = false)
        String contractorId,

        @NotNull(message = "{deal.main_borrower.main_is_null}")
        @Schema(description = "Признак наличия/отсутствия сделок", nullable = false)
        boolean main
) {
}
