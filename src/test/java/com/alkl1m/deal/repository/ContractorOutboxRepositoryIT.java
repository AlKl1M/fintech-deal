package com.alkl1m.deal.repository;

import com.alkl1m.deal.domain.entity.ContractorOutbox;
import com.alkl1m.deal.domain.enums.ContractorOutboxStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ContractorOutboxRepositoryIT {

    @Autowired
    ContractorOutboxRepository outboxRepository;

    @Autowired
    TestEntityManager entityManager;

    private ContractorOutbox outbox;

    @BeforeEach
    void setUp() {
        outbox = ContractorOutbox.builder()
                .createdDate(new Date())
                .main(true)
                .idempotentKey(UUID.randomUUID().toString())
                .status(ContractorOutboxStatus.CREATED)
                .contractorId("TEST_CONTRACTOR")
                .build();
        entityManager.persist(outbox);
    }

    @Test
    void testSave_withValidPayload_returnsSameType() {
        ContractorOutbox insertedOutbox = outboxRepository.save(outbox);
        assertEquals(insertedOutbox.getId(), outbox.getId());
        assertEquals(insertedOutbox.getContractorId(), outbox.getContractorId());
        assertEquals(insertedOutbox.isMain(), outbox.isMain());
    }

    @Test
    void testUpdate_withValidPayload_returnsSameType() {
        Date newDate = new Date();
        outbox.setCreatedDate(newDate);
        outboxRepository.save(outbox);
        assertThat(entityManager.find(ContractorOutbox.class, outbox.getId()).getCreatedDate()).isEqualTo(newDate);
    }

    @Test
    void testFindById_withValidPayload_returnsValidType() {
        entityManager.persist(outbox);
        Optional<ContractorOutbox> retrievedContractor = outboxRepository.findById(outbox.getId());
        assertThat(retrievedContractor).contains(outbox);
    }

    @Test
    void testDelete_withValidPayload_returnsNoTypeInDb() {
        entityManager.persist(outbox);
        outboxRepository.delete(outbox);
        assertThat(entityManager.find(ContractorOutbox.class, outbox.getId())).isNull();
    }

}