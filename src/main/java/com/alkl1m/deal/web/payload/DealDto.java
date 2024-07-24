package com.alkl1m.deal.web.payload;

import com.alkl1m.deal.domain.entity.Deal;
import com.alkl1m.deal.domain.entity.Status;
import com.alkl1m.deal.domain.entity.Type;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * DTO сделок.
 *
 * @param id уникальный идентификатор сделки.
 * @param description описание сделки.
 * @param agreementNumber номер договора.
 * @param agreementDate дата соглашения.
 * @param agreementStartDt дата начала действия соглашения.
 * @param availabilityDate дата доступности.
 * @param type тип сделки.
 * @param status статус сделки.
 * @param sum сумма сделки.
 * @param closeDt дата закрытия сделки.
 * @param contractors список контрагентов сделки.
 * @author alkl1m
 */
public record DealDto(

        @JsonProperty("id")
        @Schema(description = "Уникальный идентификатор сделки")
        UUID id,

        @JsonProperty("description")
        @Schema(description = "Описание сделки")
        String description,

        @JsonProperty("agreement_number")
        @Schema(description = "Номер договора")
        String agreementNumber,

        @JsonProperty("agreement_date")
        @Schema(description = "Дата соглашения")
        LocalDate agreementDate,

        @JsonProperty("agreement_start_dt")
        @Schema(description = "Дата начала действия соглашения")
        LocalDate agreementStartDt,

        @JsonProperty("availability_date")
        @Schema(description = "Дата доступности")
        LocalDate availabilityDate,

        @JsonProperty("type")
        @Schema(description = "Тип сделки")
        Type type,

        @JsonProperty("status")
        @Schema(description = "Статус сделки")
        Status status,

        @JsonProperty("sum")
        @Schema(description = "Сумма сделки")
        BigDecimal sum,

        @JsonProperty("close_dt")
        @Schema(description = "Дата закрытия сделки")
        LocalDate closeDt,

        @JsonProperty("contractors")
        @Schema(description = "Список контрагентов сделки")
        Set<ContractorDto> contractors

) {

    public static DealDto from(Deal deal, Set<ContractorDto> contractors) {
        return new DealDto(
                deal.getId(),
                deal.getDescription(),
                deal.getAgreementNumber(),
                deal.getAgreementDate(),
                deal.getAgreementStartDt(),
                deal.getAvailabilityDate(),
                deal.getType(),
                deal.getStatus(),
                deal.getSum(),
                deal.getCloseDt(),
                contractors
        );
    }
}
