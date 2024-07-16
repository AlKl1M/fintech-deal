package com.alkl1m.deal.web.payload;

import com.alkl1m.deal.domain.entity.Status;
import com.alkl1m.deal.domain.entity.Type;

import java.util.List;
import java.util.UUID;

public record DealFiltersPayload(
        UUID id,
        String description,
        String agreementNumber,
        String agreementDateRange,
        String availabilityDateRange,
        List<Type> type,
        List<Status> status,
        String closeDtRange,
        ContractorFilterDto borrower,
        ContractorFilterDto warranty
) {
}
