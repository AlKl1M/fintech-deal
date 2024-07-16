package com.alkl1m.deal.client;

import org.springframework.http.ResponseEntity;

public interface ContractorClient {
    ResponseEntity<Void> mainBorrower(String contractorId, Boolean main);
}
