package com.alkl1m.deal.service.impl;

import com.alkl1m.deal.domain.entity.Contractor;
import com.alkl1m.deal.domain.entity.Role;
import com.alkl1m.deal.domain.exception.ContractorNotFoundException;
import com.alkl1m.deal.repository.ContractorRepository;
import com.alkl1m.deal.repository.RoleRepository;
import com.alkl1m.deal.service.ContractorService;
import com.alkl1m.deal.web.payload.ContractorDto;
import com.alkl1m.deal.web.payload.NewContractorPayload;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContractorServiceImpl implements ContractorService {

    private final ContractorRepository contractorRepository;
    private final RoleRepository roleRepository;
    private static final String DEFAULT_USER_ID = "1";

    @Override
    @Transactional
    public ContractorDto saveOrUpdate(NewContractorPayload payload) {
        Contractor contractor = new Contractor();
        if (payload.id() != null && contractorRepository.existsById(payload.id())) {
            Contractor existingContractor = contractorRepository.findById(payload.id()).orElse(null);
            if (existingContractor != null) {
                contractor = updateExistingContractor(payload, existingContractor);
            }
        } else {
            contractor = createNewContractor(payload);
        }

        return ContractorDto.from(contractor);
    }

    @Override
    @Transactional
    public void deleteContractorById(UUID id) {
        Optional<Contractor> optionalContractor = contractorRepository.findById(id);
        optionalContractor.ifPresentOrElse(contractor -> {
            contractor.setActive(false);
            contractorRepository.save(contractor);
        }, () -> {
            throw new ContractorNotFoundException(String.format("Contractor with id %s not found", id));
        });
    }

    @Override
    @Transactional
    public void addRoleToContractor(UUID contractorId, String roleId) {
        Contractor contractor = contractorRepository.findById(contractorId).orElseThrow(() -> new EntityNotFoundException("Contractor not found"));
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new EntityNotFoundException("Role not found"));

        contractor.getRoles().add(role);
        contractorRepository.save(contractor);
    }

    @Override
    @Transactional
    public void deactivateRoleForContractor(UUID contractorId, String roleId) {
        contractorRepository.deleteRoleFromContractor(contractorId, roleId);
    }

    private Contractor createNewContractor(NewContractorPayload payload) {
        Contractor contractor = NewContractorPayload.toContractor(payload, DEFAULT_USER_ID);
        contractor.setId(UUID.randomUUID());
        return contractorRepository.save(contractor);
    }

    private Contractor updateExistingContractor(NewContractorPayload payload, Contractor existingContractor) {
        existingContractor.setId(payload.id());
        existingContractor.setDealId(payload.dealId());
        existingContractor.setContractorId(payload.contractorId());
        existingContractor.setName(payload.name());
        existingContractor.setInn(payload.inn());
        existingContractor.setMain(payload.main());
        existingContractor.setModifyDate(new Date());
        existingContractor.setModifyUserId(DEFAULT_USER_ID);
        return contractorRepository.save(existingContractor);
    }
}
