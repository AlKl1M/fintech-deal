package com.alkl1m.deal.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * @author alkl1m
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "deal")
public class Deal {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "description")
    private String description;

    @Column(name = "agreement_number")
    private String agreementNumber;

    @Column(name = "agreement_date")
    private LocalDate agreementDate;

    @Column(name = "agreement_start_dt")
    private LocalDate agreementStartDt;

    @Column(name = "availability_date")
    private LocalDate availabilityDate;

    @ManyToOne
    @JoinColumn(name = "deal_type")
    private Type type;

    @ManyToOne
    @JoinColumn(name = "deal_status")
    private Status status;

    @Column(name = "sum")
    private BigDecimal sum;

    @Column(name = "close_dt")
    private LocalDate closeDt;

    @ManyToMany
    @JoinTable(name = "deal_contractor",
            joinColumns = @JoinColumn(name = "deal_id"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    private Set<Contractor> contractors;

    @CreatedDate
    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    @LastModifiedDate
    @Column(name = "modify_date")
    private LocalDate modifyDate;

    @CreatedBy
    @Column(name = "create_user_id")
    private String createUserId;

    @LastModifiedBy
    @Column(name = "modify_user_id")
    private String modifyUserId;

    @JsonIgnore
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

}
