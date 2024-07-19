package com.alkl1m.deal.web.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;


/**
 * Payload для изменения статуса сделки.
 *
 * @param dealId Уникальный идентификатор сделки.
 * @param statusId Уникальный идентификатор статуса.
 * @author alkl1m
 */
public record ChangeStatusPayload(

        @NotNull(message = "{deal.status.errors.deal_id_is_null}")
        @Schema(description = "Уникальный идентификатор сделки", nullable = false)
        UUID dealId,

        @NotNull(message = "{deal.status.errors.status_id_is_null}")
        @Schema(description = "Уникальный идентификатор статуса", nullable = false)
        String statusId

) {
}
