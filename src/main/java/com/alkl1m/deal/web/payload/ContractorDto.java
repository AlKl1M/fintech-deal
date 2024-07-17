package com.alkl1m.deal.web.payload;

import com.alkl1m.deal.domain.entity.Contractor;
import com.alkl1m.deal.domain.entity.Role;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;
import java.util.UUID;

public record ContractorDto(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("contractor_id")
        String contractorId,
        @JsonProperty("name")
        String name,
        @JsonProperty("main")
        boolean main,
        @JsonProperty("roles")
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
