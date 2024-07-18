package com.alkl1m.deal.web.payload;

public record MainBorrowerRequest(
        String contractorId,
        boolean main
) {
}
