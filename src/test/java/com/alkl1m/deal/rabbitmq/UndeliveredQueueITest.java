package com.alkl1m.deal.rabbitmq;

import com.alkl1m.deal.config.MQConfiguration;
import com.alkl1m.deal.repository.ContractorRepository;
import com.alkl1m.deal.repository.DealRepository;
import com.alkl1m.deal.web.payload.UpdateContractorMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
class UndeliveredQueueITest {

    @Container
    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3-management")
            .withVhost("vhost");

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:12")
            .withReuse(true);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgreSQLContainer::getDriverClassName);
        registry.add("spring.rabbitmq.millisToResend", () -> 3000);
        registry.add("spring.rabbitmq.retryCount", () -> 0);
    }

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    RabbitListenerEndpointRegistry registry;
    @Autowired
    RabbitAdmin rabbitAdmin;

    @Autowired
    ContractorRepository contractorRepository;
    @Autowired
    DealRepository dealRepository;

    @AfterEach
    void tearDown() {
        contractorRepository.deleteAll();
        dealRepository.deleteAll();
    }

    @Test
    void testSendMessage_withDeadMessageMaxRetries_willGoToUndeliveredQueue() throws InterruptedException {
        UpdateContractorMessage message = new UpdateContractorMessage("123456789012", "newName", "111111111", ZonedDateTime.now().toString(), "1");
        rabbitTemplate.convertAndSend(MQConfiguration.DEALS_CONTRACTOR_DLX, MQConfiguration.DEALS_CONTRACTOR_NEW_DATA_QUEUE, message, m -> {
            Map<String, Object> xDeath = new HashMap<>();
            xDeath.put("count", 1);
            xDeath.put("exchange", "contractors_contractor_exchange");
            xDeath.put("queue", "deals_contractor_queue");
            xDeath.put("reason", "rejected");
            xDeath.put("routing-keys", "deals_contractor_queue");
            xDeath.put("time", System.currentTimeMillis() / 1000);
            m.getMessageProperties().getHeaders().put("x-death", xDeath);
            m.getMessageProperties().getHeaders().put("x-first-death-exchange", "contractors_contractor_exchange");
            m.getMessageProperties().getHeaders().put("x-first-death-queue", "deals_contractor_queue");
            m.getMessageProperties().getHeaders().put("x-first-death-reason", "rejected");
            m.getMessageProperties().getHeaders().put("x-last-death-exchange", "contractors_contractor_exchange");
            m.getMessageProperties().getHeaders().put("x-last-death-queue", "deals_contractor_queue");
            m.getMessageProperties().getHeaders().put("x-last-death-reason", "rejected");
            return m;
        });
        Thread.sleep(5000);

        assertEquals(1, rabbitAdmin.getQueueInfo(MQConfiguration.DEALS_CONTRACTOR_UNDELIVERED_QUEUE).getMessageCount());

    }

}
