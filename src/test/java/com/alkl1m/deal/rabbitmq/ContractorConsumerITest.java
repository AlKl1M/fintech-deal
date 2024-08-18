package com.alkl1m.deal.rabbitmq;

import com.alkl1m.deal.config.MQConfiguration;
import com.alkl1m.deal.domain.entity.Contractor;
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
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Testcontainers
@SpringBootTest
class ContractorConsumerITest {

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
    @Sql("/sql/contractors.sql")
    void testReceiveMessage_withValidMessage_returnsSavedData() {
        UpdateContractorMessage message = new UpdateContractorMessage("123456789012", "newName", "111111111", ZonedDateTime.now().toString(), "1");
        rabbitTemplate.convertAndSend(MQConfiguration.DEALS_CONTRACTOR_NEW_DATA_QUEUE, message);

        await().atMost(5, SECONDS).untilAsserted(() -> {
            Optional<Contractor> byContractorId = contractorRepository.findByContractorId(message.id());
            assertNotNull(byContractorId);
            assertNotNull(byContractorId.get().getModifyDate());
            assertEquals(byContractorId.get().getName(), message.name());
            assertEquals(byContractorId.get().getInn(), message.inn());
            assertEquals(byContractorId.get().getModifyUserId(), message.modifyUser());
        });
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testReceiveMessage_withInvalidDate_dontSaveNewData() {
        Optional<Contractor> byContractorId = contractorRepository.findByContractorId("123456789012");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
        UpdateContractorMessage message = new UpdateContractorMessage("123456789012", "newName", "111111111", zonedDateTime.toString(), "1");
        rabbitTemplate.convertAndSend(MQConfiguration.DEALS_CONTRACTOR_NEW_DATA_QUEUE, message);

        await().atMost(5, SECONDS).untilAsserted(() -> {
            Optional<Contractor> byContractorId2 = contractorRepository.findByContractorId(message.id());
            assertEquals(byContractorId.get().getContractorId(), byContractorId2.get().getContractorId());
            assertEquals(byContractorId.get().getName(), byContractorId2.get().getName());
            assertEquals(byContractorId.get().getInn(), byContractorId2.get().getInn());
        });
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testReceiveMessage_withRestartedConsumer_saveNewData() {
        registry.stop();

        UpdateContractorMessage message = new UpdateContractorMessage("123456789012", "newName", "111111111", ZonedDateTime.now().toString(), "1");
        rabbitTemplate.convertAndSend(MQConfiguration.DEALS_CONTRACTOR_NEW_DATA_QUEUE, message);

        await().atMost(5, SECONDS).untilAsserted(() -> {
            registry.getListenerContainer(MQConfiguration.DEALS_CONTRACTOR_NEW_DATA_QUEUE).start();
            assertTrue(registry.getListenerContainer(MQConfiguration.DEALS_CONTRACTOR_NEW_DATA_QUEUE).isRunning());
        });


        await().atMost(5, SECONDS).untilAsserted(() -> {
            Optional<Contractor> byContractorId = contractorRepository.findByContractorId(message.id());
            assertNotNull(byContractorId);
            assertNotNull(byContractorId.get().getModifyDate());
            assertEquals(byContractorId.get().getName(), message.name());
            assertEquals(byContractorId.get().getInn(), message.inn());
            assertEquals(byContractorId.get().getModifyUserId(), message.modifyUser());
        });
    }

    @Test
    @Sql("/sql/contractors.sql")
    void testReceiveMessage_withInvalidData_ThrowsExceptionAndGoesToDLQ() throws InterruptedException {
        UpdateContractorMessage message = new UpdateContractorMessage("123456789012", "newName", "111111111", "WRONG DATE", "1");

        rabbitTemplate.convertAndSend(MQConfiguration.DEALS_CONTRACTOR_NEW_DATA_QUEUE, message);

        Thread.sleep(5000);

        UpdateContractorMessage receivedMessage = (UpdateContractorMessage) rabbitTemplate.receiveAndConvert(MQConfiguration.DEALS_CONTRACTOR_DLQ);

        assertNotNull(receivedMessage);
        assertEquals(receivedMessage.id(), message.id());
        assertEquals(receivedMessage.name(), message.name());
        assertEquals(receivedMessage.inn(), message.inn());
    }

}
