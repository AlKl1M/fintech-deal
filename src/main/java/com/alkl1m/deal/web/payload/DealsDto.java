package com.alkl1m.deal.web.payload;

import org.springframework.data.domain.Page;

public record DealsDto(
        Page<DealDto> deals
) {
    public static DealsDto from(Page<DealDto> deals) {
        return new DealsDto(deals);
    }
}
