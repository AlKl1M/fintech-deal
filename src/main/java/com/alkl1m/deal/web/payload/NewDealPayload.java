package com.alkl1m.deal.web.payload;

import com.alkl1m.deal.domain.entity.Deal;
import com.alkl1m.deal.domain.entity.Status;
import com.alkl1m.deal.domain.entity.Type;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Payload для создания новой сделки.
 *
 * @param id уникальный идентификатор сделки.
 * @param description описание сделки.
 * @param agreementNumber номер договора.
 * @param agreementDate дата договора.
 * @param agreementStartDt дата и время вступления соглашения в силу.
 * @param availabilityDate срок действия сделки.
 * @param typeId уникальный идентификатор типа сделки.
 * @param sum сумма сделки.
 * @param closeDt дата закрытия сделки.
 * @author alkl1m
 */
public record NewDealPayload(

        @Schema(description = "Уникальный идентификатор сделки")
        UUID id,

        @Schema(description = "Описание сделки")
        String description,

        @Schema(description = "Номер договора")
        String agreementNumber,

        @Schema(description = "Дата договора")
        Date agreementDate,

        @Schema(description = "Дата и время вступления соглашения в силу")
        Date agreementStartDt,

        @Schema(description = "Срок действия сделки")
        Date availabilityDate,

        @Schema(description = "Уникальный идентификатор типа сделки")
        String typeId,

        @Schema(description = "Сумма сделки")
        BigDecimal sum,

        @Schema(description = "Дата закрытия сделки")
        Date closeDt
) {
    public static Deal toDeal(NewDealPayload payload, Type type, Status status, String userId) {
        return Deal.builder()
                .id(payload.id)
                .description(payload.description())
                .agreementNumber(payload.agreementNumber)
                .agreementDate(payload.agreementDate)
                .agreementStartDt(payload.agreementStartDt)
                .availabilityDate(payload.availabilityDate)
                .type(type)
                .status(status)
                .sum(payload.sum)
                .closeDt(payload.closeDt)
                .createDate(new Date())
                .createUserId(userId)
                .isActive(true)
                .build();
    }
}
