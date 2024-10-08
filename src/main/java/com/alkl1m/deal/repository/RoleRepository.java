package com.alkl1m.deal.repository;

import com.alkl1m.deal.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author alkl1m
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

}
