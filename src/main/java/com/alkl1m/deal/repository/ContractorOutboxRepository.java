package com.alkl1m.deal.repository;

import com.alkl1m.deal.domain.entity.ContractorOutbox;
import com.alkl1m.deal.domain.enums.ContractorOutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface ContractorOutboxRepository extends JpaRepository<ContractorOutbox, Long> {

    List<ContractorOutbox> findByStatus(ContractorOutboxStatus status);

}
