package com.alkl1m.deal.web.payload;

public record UpdateContractorMessage(
        String id,
        String name,
        String inn
) {
}
