package com.alkl1m.deal.web.payload;

import com.alkl1m.deal.domain.entity.Deal;
import com.alkl1m.deal.domain.entity.Status;
import com.alkl1m.deal.domain.entity.Type;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public record DealDto(
        UUID id,
        String description,
        String agreementNumber,
        Date agreementStartDt,
        Date availabilityDate,
        Type type,
        Status status,
        BigDecimal sum,
        Date closeDt,
        List<ContractorDto> contractors

) {

    public static DealDto from(Deal deal, List<ContractorDto> contractors) {
        return new DealDto(
                deal.getId(),
                deal.getDescription(),
                deal.getAgreementNumber(),
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
