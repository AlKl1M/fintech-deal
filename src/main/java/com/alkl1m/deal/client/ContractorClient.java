package com.alkl1m.deal.client;

import com.alkl1m.deal.web.payload.MainBorrowerRequest;
import org.springframework.http.ResponseEntity;

/**
 * Клиент, который использует RestClient для отправки запросов в сервисы контрагента.
 *
 * @author alkl1m
 */
public interface ContractorClient {

    /**
     * Отправляет информацию о том, является ли контрагент основным заемщиком.
     *
     * @param payload информация о том, является ли контрагент, айди которого передано
     *                основным заемщиком.
     * @return ответ сервиса контрагента.
     */
    ResponseEntity<Void> mainBorrower(MainBorrowerRequest payload);

}
