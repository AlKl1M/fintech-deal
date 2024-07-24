package com.alkl1m.deal.repository;

import com.alkl1m.deal.TestBeans;
import com.alkl1m.deal.domain.entity.ContractorOutbox;
import com.alkl1m.deal.domain.enums.ContractorOutboxStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import(TestBeans.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ContractorOutboxRepositoryTest {

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

    @Test
    void testFindByStatus_withValidStatus_returnsPageOfResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ContractorOutbox> results = outboxRepository.findByStatus(ContractorOutboxStatus.CREATED, pageable);

        assertThat(results).isNotEmpty();
        assertThat(results.getContent()).contains(outbox);
    }

}