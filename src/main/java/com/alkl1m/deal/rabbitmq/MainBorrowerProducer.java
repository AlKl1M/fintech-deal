package com.alkl1m.deal.rabbitmq;

import com.alkl1m.deal.config.MQConfiguration;
import com.alkl1m.deal.web.payload.MainBorrowerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainBorrowerProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(MainBorrowerRequest msg) {
        rabbitTemplate.convertAndSend(MQConfiguration.DEALS_UPDATE_MAIN_BORROWER_EXC, MQConfiguration.CONTRACTOR_UPDATE_MAIN_BORROWER_QUEUE, msg);
    }

}
