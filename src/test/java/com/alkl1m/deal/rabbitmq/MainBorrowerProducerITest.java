package com.alkl1m.deal.rabbitmq;

import com.alkl1m.deal.config.MQConfiguration;
import com.alkl1m.deal.web.payload.MainBorrowerRequest;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class MainBorrowerProducerITest {

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
    }

    @Autowired
    MainBorrowerProducer mainBorrowerProducer;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RabbitAdmin rabbitAdmin;

    @Test
    void testSendMessage_withValidPayload_returnsValidData() throws InterruptedException {

        MainBorrowerRequest request = new MainBorrowerRequest("1", true);
        mainBorrowerProducer.sendMessage(request);

        Thread.sleep(5000);

        MainBorrowerRequest receivedMessage = (MainBorrowerRequest) rabbitTemplate.receiveAndConvert(MQConfiguration.UPDATE_MAIN_BORROWER_QUEUE);

        assertThat(receivedMessage).isNotNull();
        assertThat(receivedMessage.contractorId()).isEqualTo("1");
        assertThat(receivedMessage.main()).isTrue();
    }

}
