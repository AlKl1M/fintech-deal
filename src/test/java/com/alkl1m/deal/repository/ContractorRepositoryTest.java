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
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(TestBeans.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ContractorRepositoryTest {

    @Autowired
    ContractorRepository contractorRepository;

    @Autowired
    TestEntityManager entityManager;

    private Type type;
    private Status status;
    private Deal newDeal;
    private Contractor newContractor;

    @BeforeEach
    void setUp() {
        type = Type.builder()
                .id("TEST")
                .name("Test type")
                .isActive(true)
                .build();
        status = Status.builder()
                .id("TEST")
                .name("Test status")
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

        newContractor = Contractor.builder()
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
    }

    @Test
    void testSave_withValidPayload_returnsSameType() {
        Contractor insertedContractor = contractorRepository.save(newContractor);
        assertEquals(insertedContractor.getId(), newContractor.getId());
        assertEquals(insertedContractor.getDealId(), newContractor.getDealId());
        assertEquals(insertedContractor.getContractorId(), newContractor.getContractorId());
        assertEquals(insertedContractor.getName(), newContractor.getName());
        assertEquals(insertedContractor.isMain(), newContractor.isMain());
    }

    @Test
    void testUpdate_withValidPayload_returnsSameType() {
        String newName = "New name";
        newContractor.setName(newName);
        contractorRepository.save(newContractor);
        assertThat(entityManager.find(Contractor.class, newContractor.getId()).getName()).isEqualTo(newName);
    }

    @Test
    void testFindById_withValidPayload_returnsValidType() {
        entityManager.persist(newContractor);
        Optional<Contractor> retrievedContractor = contractorRepository.findById(newContractor.getId());
        assertThat(retrievedContractor).contains(newContractor);
    }

    @Test
    void testDelete_withValidPayload_returnsNoTypeInDb() {
        entityManager.persist(newContractor);
        contractorRepository.delete(newContractor);
        assertThat(entityManager.find(Contractor.class, newContractor.getId())).isNull();
    }

    @Test
    void testExistsByDealIdAndMainTrue_withExistingData_returnsTrue() {
        UUID dealId = newDeal.getId();
        boolean exists = contractorRepository.existsByDealIdAndMainTrue(dealId);
        assertTrue(exists);
    }

}