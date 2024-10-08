package com.alkl1m.deal.domain.entity;

import com.alkl1m.deal.domain.enums.ContractorOutboxStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Сущность для реализации outbox pattern для Contractor.
 * Содержит информацию о сущности контрагентах и поля для отправки.
 *
 * @author alkl1m
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "contractor_outbox")
public class ContractorOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "main")
    private boolean main;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ContractorOutboxStatus status;

    @Column(name = "contractor_id")
    private String contractorId;

}
