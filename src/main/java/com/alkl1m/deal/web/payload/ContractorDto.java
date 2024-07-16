package com.alkl1m.deal.web.payload;

import com.alkl1m.deal.domain.entity.Contractor;
import com.alkl1m.deal.domain.entity.Role;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record ContractorDto(
        UUID id,
        String contractorId,
        String name,
        boolean main,
        Set<Role> roles
) {

    public static ContractorDto from (Contractor contractor) {
        return new ContractorDto(
                contractor.getId(),
                contractor.getContractorId(),
                contractor.getName(),
                contractor.isMain(),
                contractor.getRoles()
        );
    }

}
