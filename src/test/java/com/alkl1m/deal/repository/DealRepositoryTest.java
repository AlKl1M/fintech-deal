package com.alkl1m.deal.repository;

import com.alkl1m.deal.TestBeans;
import com.alkl1m.deal.domain.entity.Contractor;
import com.alkl1m.deal.domain.entity.Deal;
import com.alkl1m.deal.domain.entity.Status;
import com.alkl1m.deal.domain.entity.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import(TestBeans.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DealRepositoryTest {

    @Autowired
    DealRepository dealRepository;

    @Autowired
    ContractorRepository contractorRepository;

    @Autowired
    TestEntityManager entityManager;

    private Type type;
    private Status status;
    private Deal newDeal;

    @BeforeEach
    void setUp() {
        type = Type.builder()
                .id("TEST")
                .name("Test type")
                .isActive(true)
                .build();
        status = Status.builder()
                .id("TEST")
                .name("TEST status")
                .isActive(true)
                .build();
        entityManager.persist(type);
        entityManager.persist(status);
        newDeal = Deal.builder()
                .id(UUID.randomUUID())
                .description("TEST DESCRIPTION")
                .agreementNumber("TESTNUMBER")
                .agreementDate(LocalDate.now())
                .agreementStartDt(LocalDate.now())
                .availabilityDate(LocalDate.now())
                .type(type)
                .status(status)
                .sum(new BigDecimal(1000.00))
                .closeDt(null)
                .createDate(LocalDate.now())
                .modifyDate(null)
                .createUserId("TEST USER")
                .modifyUserId(null)
                .isActive(true)
                .build();
        entityManager.persist(newDeal);
    }

    @Test
    void testSave_withValidPayload_returnsSameType() {
        Deal insertedDeal = dealRepository.save(newDeal);
        assertEquals(insertedDeal.getId(), newDeal.getId());
        assertEquals(insertedDeal.getDescription(), newDeal.getDescription());
        assertEquals(insertedDeal.getAgreementNumber(), newDeal.getAgreementNumber());
        assertEquals(insertedDeal.getType(), newDeal.getType());
        assertEquals(insertedDeal.getStatus(), newDeal.getStatus());
        assertEquals(insertedDeal.isActive(), newDeal.isActive());
    }

    @Test
    void testUpdate_withValidPayload_returnsSameType() {
        String newDescription = "New description";
        newDeal.setDescription(newDescription);
        dealRepository.save(newDeal);
        assertThat(entityManager.find(Deal.class, newDeal.getId()).getDescription()).isEqualTo(newDescription);
    }

    @Test
    void testFindById_withValidPayload_returnsValidType() {
        entityManager.persist(newDeal);
        Optional<Deal> retrievedType = dealRepository.findById(newDeal.getId());
        assertThat(retrievedType).contains(newDeal);
    }

    @Test
    void testDelete_withValidPayload_returnsNoTypeInDb() {
        entityManager.persist(newDeal);
        dealRepository.delete(newDeal);
        assertThat(entityManager.find(Deal.class, newDeal.getId())).isNull();
    }

    @Test
    void testCheckIfDealExists_withValidPayload_returns() {
        Contractor newContractor = Contractor.builder()
                .id(UUID.randomUUID())
                .dealId(newDeal.getId())
                .contractorId("TEST_CONTRACTOR_ID")
                .name("TEST CONTRACTOR")
                .inn("123456789")
                .main(true)
                .createDate(LocalDate.now())
                .modifyDate(null)
                .createUserId("TEST USER")
                .modifyUserId(null)
                .isActive(true)
                .build();
        entityManager.persist(newContractor);

        int dealCount = dealRepository.checkIfDealExists(newContractor.getContractorId());
        assertThat(dealCount).isZero();
    }

}