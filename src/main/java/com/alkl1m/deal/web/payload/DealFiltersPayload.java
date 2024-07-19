package com.alkl1m.deal.web.payload;

import com.alkl1m.deal.domain.entity.Status;
import com.alkl1m.deal.domain.entity.Type;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

/**
 * Список фильтров для сделок.
 *
 * @param id уникальный идентификатор сделки.
 * @param description описание сделки.
 * @param agreementNumber номер договора.
 * @param agreementDateRange диапазон дат начала действия договора.
 * @param availabilityDateRange диапазон дат начала доступности.
 * @param type список типов.
 * @param status список статусов.
 * @param closeDtRange диапазон дат закрытия сделки.
 * @param borrower заемщик.
 * @param warranty поручитель.
 * @author alkl1m
 */
public record DealFiltersPayload(

        @Schema(description = "Уникальный идентификатор сделки")
        UUID id,

        @Schema(description = "Описание сделки")
        String description,

        @Schema(description = "Номер договора")
        String agreementNumber,

        @Schema(description = "Диапазон дат начала действия договора")
        String agreementDateRange,

        @Schema(description = "Диапазон дат начала доступности")
        String availabilityDateRange,

        @Schema(description = "Список типов")
        List<Type> type,

        @Schema(description = "Список статусов")
        List<Status> status,

        @Schema(description = "Диапазон дат закрытия сделки")
        String closeDtRange,

        @Schema(description = "Фильтр по заемщикам")
        ContractorFilterDto borrower,

        @Schema(description = "Фильтр по поручителям")
        ContractorFilterDto warranty
) {
}
