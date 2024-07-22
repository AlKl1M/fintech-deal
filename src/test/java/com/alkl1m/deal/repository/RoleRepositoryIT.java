package com.alkl1m.deal.repository;

import com.alkl1m.deal.TestBeans;
import com.alkl1m.deal.domain.entity.Role;
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
class RoleRepositoryIT {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void testSave_withValidPayload_returnsSameType() {
        Role newRole = Role.builder()
                .id("TEST")
                .name("Test role")
                .category("TEST")
                .isActive(true)
                .build();

        Role insertedRole = roleRepository.save(newRole);
        assertEquals(insertedRole.getId(), newRole.getId());
        assertEquals(insertedRole.getName(), newRole.getName());
        assertEquals(insertedRole.getCategory(), newRole.getCategory());
        assertEquals(insertedRole.isActive(), newRole.isActive());
    }

    @Test
    void testUpdate_withValidPayload_returnsSameType() {
        Role newRole = Role.builder()
                .id("TEST")
                .name("Test role")
                .category("TEST")
                .isActive(true)
                .build();
        entityManager.persist(newRole);
        String newName = "New role name";
        newRole.setName(newName);
        roleRepository.save(newRole);
        assertThat(entityManager.find(Role.class, newRole.getId()).getName()).isEqualTo(newName);
    }

    @Test
    void testFindById_withValidPayload_returnsValidType() {
        Role newRole = Role.builder()
                .id("TEST")
                .name("Test role")
                .category("TEST")
                .isActive(true)
                .build();
        entityManager.persist(newRole);
        Optional<Role> retrievedType = roleRepository.findById(newRole.getId());
        assertThat(retrievedType).contains(newRole);
    }

    @Test
    void testDelete_withValidPayload_returnsNoTypeInDb() {
        Role newRole = Role.builder()
                .id("TEST")
                .name("Test role")
                .category("TEST")
                .isActive(true)
                .build();
        entityManager.persist(newRole);
        roleRepository.delete(newRole);
        assertThat(entityManager.find(Role.class, newRole.getId())).isNull();
    }

}
