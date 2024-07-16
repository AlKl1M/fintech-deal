package com.alkl1m.deal.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class WebClientContractorClient implements ContractorClient {

    private final RestClient restClient;


    @Override
    public ResponseEntity mainBorrower(String contractorId, Boolean main) {
        return this.restClient
                .patch()
                .uri("/contractor/main-borrower?contractorId={contractorId}&main={main}", contractorId, main)
                .retrieve()
                .body(ResponseEntity.class);
    }
}
