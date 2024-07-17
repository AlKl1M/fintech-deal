package com.alkl1m.deal.repository;

import com.alkl1m.deal.domain.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DealRepository extends JpaRepository<Deal, UUID>, JpaSpecificationExecutor<Deal> {

    @Query(value = """
            SELECT COUNT(*) FROM deal.deal_contractor dc
                        INNER JOIN deal.deal d ON dc.deal_id = d.id
                        INNER JOIN deal.deal_status ds ON d.deal_status = ds.id
                        WHERE dc.contractor_id = :contractorId AND d.deal_status = 'ACTIVE' AND dc.is_active = true
            """, nativeQuery = true)
    int checkIfDealExists(@Param("contractorId") String contractorId);

}
