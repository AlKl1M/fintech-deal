package com.alkl1m.deal.web.payload;

import com.alkl1m.deal.domain.entity.Contractor;
import com.alkl1m.deal.domain.entity.Role;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public record NewContractorPayload(
        UUID id,
        UUID dealId,
        String contractorId,
        String name,
        String inn,
        boolean main,
        Set<Role> roles
) {

    public static Contractor toContractor(NewContractorPayload payload, String userId) {
        return Contractor.builder()
                .id(payload.id)
                .dealId(payload.dealId)
                .contractorId(payload.contractorId)
                .name(payload.name)
                .inn(payload.inn)
                .main(payload.main)
                .roles(payload.roles)
                .createDate(new Date())
                .createUserId(userId)
                .isActive(true)
                .build();
    }

}
