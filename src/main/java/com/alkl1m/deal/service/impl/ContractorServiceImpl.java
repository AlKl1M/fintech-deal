package com.alkl1m.deal.service.impl;

import com.alkl1m.deal.domain.entity.Contractor;
import com.alkl1m.deal.domain.entity.Role;
import com.alkl1m.deal.repository.ContractorRepository;
import com.alkl1m.deal.repository.DealRepository;
import com.alkl1m.deal.repository.RoleRepository;
import com.alkl1m.deal.service.ContractorService;
import com.alkl1m.deal.web.payload.ContractorDto;
import com.alkl1m.deal.web.payload.MainBorrowerMessage;
import com.alkl1m.deal.web.payload.NewContractorPayload;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContractorServiceImpl implements ContractorService {

    private final DealRepository dealRepository;
    private final ContractorRepository contractorRepository;
    private final RoleRepository roleRepository;
    private static final String DEFAULT_USER_ID = "1";

    @Override
    @Transactional
    public ContractorDto saveOrUpdate(NewContractorPayload payload) {
        Contractor contractor = new Contractor();
        if (payload.main() && contractorRepository.existsByDealIdAndMainTrue(payload.dealId())) {
            throw new IllegalStateException("You can't create more than one main contractor for each deal");
        }
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
    public MainBorrowerMessage deleteContractorById(UUID id) {
        Optional<Contractor> optionalContractor = contractorRepository.findById(id);
        if (optionalContractor.isPresent()) {
            boolean canDelete = dealRepository.checkIfDealExists(optionalContractor.get().getContractorId()) <= 1;
            optionalContractor.get().setActive(!canDelete);
            contractorRepository.save(optionalContractor.get());
            return new MainBorrowerMessage(optionalContractor.get().getContractorId(), canDelete);
        }
        return new MainBorrowerMessage(null, false);
    }

    @Override
    @Transactional
    public void addRoleToContractor(UUID contractorId, String roleId) {
        Contractor contractor = contractorRepository.findById(contractorId).orElseThrow(
                () -> new EntityNotFoundException("Contractor not found")
        );
        Role role = roleRepository.findById(roleId).orElseThrow(
                () -> new EntityNotFoundException("Role not found")
        );

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
