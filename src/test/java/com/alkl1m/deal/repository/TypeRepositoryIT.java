package com.alkl1m.deal.repository;

import com.alkl1m.deal.TestBeans;
import com.alkl1m.deal.domain.entity.Type;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import(TestBeans.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TypeRepositoryIT {


    @Autowired
    TypeRepository typeRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void testSave_withValidPayload_returnsSameType() {
        Type newType = Type.builder()
                .id("TEST")
                .name("Test type")
                .isActive(true)
                .build();

        Type insertedType = typeRepository.save(newType);
        assertEquals(insertedType.getId(), newType.getId());
        assertEquals(insertedType.getName(), newType.getName());
        assertEquals(insertedType.isActive(), newType.isActive());
    }

    @Test
    void testUpdate_withValidPayload_returnsSameType() {
        Type newType = Type.builder()
                .id("TEST")
                .name("Test type")
                .isActive(true)
                .build();
        entityManager.persist(newType);
        String newName = "New type name";
        newType.setName(newName);
        typeRepository.save(newType);
        assertThat(entityManager.find(Type.class, newType.getId()).getName()).isEqualTo(newName);
    }

    @Test
    void testFindById_withValidPayload_returnsValidType() {
        Type newType = Type.builder()
                .id("TEST")
                .name("Test type")
                .isActive(true)
                .build();
        entityManager.persist(newType);
        Optional<Type> retrievedType = typeRepository.findById(newType.getId());
        assertThat(retrievedType).contains(newType);
    }

    @Test
    void testDelete_withValidPayload_returnsNoTypeInDb() {
        Type newType = Type.builder()
                .id("TEST")
                .name("Test type")
                .isActive(true)
                .build();
        entityManager.persist(newType);
        typeRepository.delete(newType);
        assertThat(entityManager.find(Type.class, newType.getId())).isNull();
    }

}