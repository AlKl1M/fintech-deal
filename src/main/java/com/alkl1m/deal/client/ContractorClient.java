package com.alkl1m.deal.client;

import com.alkl1m.deal.web.payload.MainBorrowerRequest;
import org.springframework.http.ResponseEntity;

public interface ContractorClient {
    ResponseEntity<Void> mainBorrower(MainBorrowerRequest payload);
}
