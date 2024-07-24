package com.alkl1m.deal.repository;

import com.alkl1m.deal.domain.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author alkl1m
 */
@Repository
public interface DealRepository extends JpaRepository<Deal, UUID>, JpaSpecificationExecutor<Deal> {

    @Query("SELECT COUNT(d) FROM Deal d " +
            "JOIN d.contractors dc " +
            "JOIN d.status ds " +
            "WHERE dc.contractorId = :contractorId AND ds.name = 'ACTIVE' AND dc.isActive = true")
    int checkIfDealExists(@Param("contractorId") String contractorId);

}
