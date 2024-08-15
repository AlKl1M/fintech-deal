package com.alkl1m.deal.repository;

import com.alkl1m.deal.domain.entity.Contractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author alkl1m
 */
@Repository
public interface ContractorRepository extends JpaRepository<Contractor, UUID> {

    boolean existsByDealIdAndMainTrue(@Param("dealId") UUID dealId);

    @Modifying
    @Query(value = """
            UPDATE contractor_to_role SET is_active = false
            WHERE deal_contractor = ?1 AND contractor_role = ?2
            """, nativeQuery = true)
    void deleteRoleFromContractor(UUID contractorId, String roleId);

    Optional<Contractor> findByContractorId(String contractorId);
}
