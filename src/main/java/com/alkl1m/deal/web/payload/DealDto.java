package com.alkl1m.deal.web.payload;

import com.alkl1m.deal.domain.entity.Deal;
import com.alkl1m.deal.domain.entity.Status;
import com.alkl1m.deal.domain.entity.Type;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public record DealDto(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("description")
        String description,
        @JsonProperty("agreement_number")
        String agreementNumber,
        @JsonProperty("agreement_date")
        Date agreementDate,
        @JsonProperty("agreement_start_dt")
        Date agreementStartDt,
        @JsonProperty("availability_date")
        Date availabilityDate,
        @JsonProperty("type")
        Type type,
        @JsonProperty("status")
        Status status,
        @JsonProperty("sum")
        BigDecimal sum,
        @JsonProperty("close_dt")
        Date closeDt,
        @JsonProperty("contractors")
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
