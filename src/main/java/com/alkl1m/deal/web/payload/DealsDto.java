package com.alkl1m.deal.web.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

/**
 * DTO сделок найденных по фильтрам.
 *
 * @param deals список сделок.
 * @author alkl1m
 */
public record DealsDto(

        @Schema(description = "Список сделок")
        Page<DealDto> deals

) {
    public static DealsDto from(Page<DealDto> deals) {
        return new DealsDto(deals);
    }
}
