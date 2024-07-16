package com.alkl1m.deal.web.payload;

import java.util.UUID;

public record ContractorFilterDto(
        String contractorId,
        String name,
        String inn
) {
}
