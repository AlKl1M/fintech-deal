package com.alkl1m.deal.domain.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Тело исключения для вывода в advice.
 *
 * @author alkl1m
 */
@Getter
@Setter
@AllArgsConstructor
public class ExceptionBody {

    @Schema(description = "Список сообщений (текст исключения)")
    private String message;
    @Schema(description = "Мапа ошибок")
    private Map<String, String> errors;

    public ExceptionBody(
            final String message
    ) {
        this.message = message;
    }

}