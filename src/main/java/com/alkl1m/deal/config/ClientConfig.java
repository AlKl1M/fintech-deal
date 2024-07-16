package com.alkl1m.deal.config;

import com.alkl1m.deal.client.WebClientContractorClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    public WebClientContractorClient contractorsRestClient(
            @Value("${services.contractor.uri:http://localhost:8080}") String contractorBaseUri
    ) {
        return new WebClientContractorClient(RestClient.builder()
                .baseUrl(contractorBaseUri)
                .build());
    }

}
