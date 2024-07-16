package com.alkl1m.deal.web.payload;

import com.alkl1m.deal.domain.entity.Deal;
import com.alkl1m.deal.domain.entity.Status;
import com.alkl1m.deal.domain.entity.Type;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public record NewDealPayload(
        UUID id,
        String description,
        String agreementNumber,
        Date agreementDate,
        Date agreementStartDt,
        Date availabilityDate,
        String typeId,
        BigDecimal sum,
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
