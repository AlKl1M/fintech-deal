package com.alkl1m.deal.client;

import com.alkl1m.deal.web.payload.MainBorrowerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class WebClientContractorClient implements ContractorClient {

    private final RestClient restClient;

    @Override
    public ResponseEntity<Void> mainBorrower(MainBorrowerRequest payload) {
        return this.restClient
                .patch()
                .uri("/contractor/main-borrower")
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }
}
