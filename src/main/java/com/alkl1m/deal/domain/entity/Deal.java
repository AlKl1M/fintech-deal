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

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "deal", name = "deal")
public class Deal {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "description")
    private String description;

    @Column(name = "agreement_number")
    private String agreementNumber;

    @Column(name = "agreement_date")
    private Date agreementDate;

    @Column(name = "agreement_start_dt")
    private Date agreementStartDt;

    @Column(name = "availability_date")
    private Date availabilityDate;

    @ManyToOne
    @JoinColumn(name = "deal_type")
    private Type type;

    @ManyToOne
    @JoinColumn(name = "deal_status")
    private Status status;

    @Column(name = "sum")
    private BigDecimal sum;

    @Column(name = "close_dt")
    private Date closeDt;

    @ManyToMany
    @JoinTable(name = "deal_contractor",
            joinColumns = @JoinColumn(name = "deal_id"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    private Set<Contractor> contractors;

    @Column(name = "create_date", nullable = false)
    private Date createDate;

    @Column(name = "modify_date")
    private Date modifyDate;

    @Column(name = "create_user_id")
    private String createUserId;

    @Column(name = "modify_user_id")
    private String modifyUserId;

    @JsonIgnore
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

}
