package com.alkl1m.deal.repository;

import com.alkl1m.deal.domain.entity.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StatusRepositoryIT {

    @Autowired
    StatusRepository statusRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void testSave_withValidPayload_returnsSameType() {
        Status newStatus = Status.builder()
                .id("TEST")
                .name("Test status")
                .isActive(true)
                .build();

        Status insertedStatus = statusRepository.save(newStatus);
        assertEquals(insertedStatus.getId(), newStatus.getId());
        assertEquals(insertedStatus.getName(), newStatus.getName());
        assertEquals(insertedStatus.isActive(), newStatus.isActive());
    }

    @Test
    void testUpdate_withValidPayload_returnsSameType() {
        Status newStatus = Status.builder()
                .id("TEST")
                .name("Test status")
                .isActive(true)
                .build();
        entityManager.persist(newStatus);
        String newName = "New status name";
        newStatus.setName(newName);
        statusRepository.save(newStatus);
        assertThat(entityManager.find(Status.class, newStatus.getId()).getName()).isEqualTo(newName);
    }

    @Test
    void testFindById_withValidPayload_returnsValidType() {
        Status newStatus = Status.builder()
                .id("TEST")
                .name("Test status")
                .isActive(true)
                .build();
        entityManager.persist(newStatus);
        Optional<Status> retrievedType = statusRepository.findById(newStatus.getId());
        assertThat(retrievedType).contains(newStatus);
    }

    @Test
    void testDelete_withValidPayload_returnsNoTypeInDb() {
        Status newStatus = Status.builder()
                .id("TEST")
                .name("Test status")
                .isActive(true)
                .build();
        entityManager.persist(newStatus);
        statusRepository.delete(newStatus);
        assertThat(entityManager.find(Status.class, newStatus.getId())).isNull();
    }

}