package com.alkl1m.deal.web.payload;

import java.util.UUID;

public record ChangeStatusPayload(
        UUID dealId,
        String statusId
) {
}
