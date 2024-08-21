package com.alkl1m.deal.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.ZonedDateTime;
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
@Table(name = "deal_contractor")
public class Contractor {

    @Id
    private UUID id;

    @Column(name = "deal_id")
    private UUID dealId;

    @Column(name = "contractor_id", nullable = false)
    private String contractorId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "inn")
    private String inn;

    @Column(name = "main", nullable = false)
    private boolean main = false;

    @ManyToMany
    @JoinTable(name = "contractor_to_role",
    joinColumns = @JoinColumn(name = "deal_contractor"),
    inverseJoinColumns = @JoinColumn(name = "contractor_role"))
    private Set<Role> roles;

    @CreatedDate
    @Column(name = "create_date")
    private ZonedDateTime createDate;

    @LastModifiedBy
    @Column(name = "modify_date")
    private ZonedDateTime modifyDate;

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
