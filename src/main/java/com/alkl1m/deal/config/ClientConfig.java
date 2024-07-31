package com.alkl1m.deal.config;

import com.alkl1m.authutilsspringbootautoconfigure.security.interceptor.JwtTokenInterceptor;
import com.alkl1m.authutilsspringbootautoconfigure.util.JwtUtils;
import com.alkl1m.deal.client.WebClientContractorClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Конфигурационный класс для настройки RestClient.
 *
 * @author alkl1m
 */
@Configuration
@RequiredArgsConstructor
public class ClientConfig {

    private final JwtUtils jwtUtils;

    /**
     * @param contractorBaseUri URI сервиса контрагентов.
     * @return клиент для обращения к сервису контрагентов.
     */
    @Bean
    public WebClientContractorClient contractorsRestClient(
            @Value("${services.contractor.uri:http://localhost:8080}") String contractorBaseUri
    ) {
        return new WebClientContractorClient(RestClient.builder()
                .baseUrl(contractorBaseUri)
                .requestInterceptor(new JwtTokenInterceptor(jwtUtils))
                .build());
    }

}
