package com.alkl1m.deal.rabbitmq;

import com.alkl1m.deal.web.payload.MainBorrowerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContractorProducer {
    private final RabbitTemplate rabbitTemplate;
    @Value("${deal.contractorExchange}")
    private String contractorExchange;
    @Value("${deal.contractorRoutingKey}")
    private String contractorRoutingKey;

    public void sendMessage(MainBorrowerRequest msg) {
        rabbitTemplate.convertAndSend(contractorExchange, contractorRoutingKey, msg);
    }
}
