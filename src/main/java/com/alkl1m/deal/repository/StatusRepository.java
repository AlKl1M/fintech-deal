package com.alkl1m.deal.repository;

import com.alkl1m.deal.domain.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author alkl1m
 */
@Repository
public interface StatusRepository extends JpaRepository<Status, String> {
}
