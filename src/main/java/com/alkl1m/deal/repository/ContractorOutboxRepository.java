package com.alkl1m.deal.repository;

import com.alkl1m.deal.domain.entity.ContractorOutbox;
import com.alkl1m.deal.domain.enums.ContractorOutboxStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractorOutboxRepository extends JpaRepository<ContractorOutbox, Long> {

    Page<ContractorOutbox> findByStatus(ContractorOutboxStatus status, Pageable pageable);

}