package com.alkl1m.deal.web.controller;

import com.alkl1m.deal.domain.exception.ExceptionBody;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author alkl1m
 */
@RestControllerAdvice
public class ControllerAdvice {
    /**
     * Метод для обработки IllegalStateException.
     *
     * @param e исключение IllegalStateException.
     * @return объект ExceptionBody с сообщением об ошибке.
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleIllegalState(
            final IllegalStateException e
    ) {
        return new ExceptionBody(e.getMessage());
    }

    /**
     * Метод для обработки MethodArgumentNotValidException.
     *
     * @param e исключение MethodArgumentNotValidException.
     * @return объект ExceptionBody с сообщением об ошибке и деталями валидации.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleMethodArgumentNotValid(
            final MethodArgumentNotValidException e
    ) {
        ExceptionBody exceptionBody = new ExceptionBody("Validation failed.");
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        exceptionBody.setErrors(errors.stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existingMessage, newMessage) ->
                                existingMessage + " " + newMessage
                )));
        return exceptionBody;
    }

    /**
     * Метод для обработки EntityNotFoundException.
     *
     * @param e исключение EntityNotFoundException.
     * @return объект ExceptionBody с сообщением об ошибке.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleContractorNotFound(
            final EntityNotFoundException e
    ) {
        return new ExceptionBody(e.getMessage());
    }
}
