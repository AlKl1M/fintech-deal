package com.alkl1m.deal.service.impl;

import com.alkl1m.deal.domain.entity.Contractor;
import com.alkl1m.deal.domain.entity.Role;
import com.alkl1m.deal.repository.ContractorRepository;
import com.alkl1m.deal.repository.DealRepository;
import com.alkl1m.deal.repository.RoleRepository;
import com.alkl1m.deal.service.ContractorOutboxService;
import com.alkl1m.deal.service.ContractorService;
import com.alkl1m.deal.web.payload.ContractorDto;
import com.alkl1m.deal.web.payload.NewContractorPayload;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Реализация сервиса для работы с контрактными исполнителями.
 *
 * @author alkl1m
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContractorServiceImpl implements ContractorService {

    private final DealRepository dealRepository;
    private final ContractorRepository contractorRepository;
    private final RoleRepository roleRepository;
    private final ContractorOutboxService outboxService;
    private static final String DEFAULT_USER_ID = "1";

    /**
     * Метод saveOrUpdate сохраняет или обновляет данные контрактного исполнителя на основе переданных данных.
     *
     * @param payload данные нового контрактного исполнителя
     * @return объект ContractorDto, представляющий сохраненного или обновленного контрактного исполнителя
     */
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

    /**
     * Метод deleteContractorById удаляет контрактного исполнителя по указанному идентификатору.
     *
     * @param id идентификатор контрактного исполнителя для удаления
     */
    @Override
    @Transactional
    public void deleteContractorById(UUID id) {
        Contractor contractor = contractorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contractor not found with id: " + id));

        boolean canDelete = dealRepository.checkIfDealExists(contractor.getContractorId()) <= 1;

        contractor.setActive(false);

        if (canDelete) {
            outboxService.save(false, contractor.getContractorId());
        }

        contractorRepository.save(contractor);
    }

    /**
     * Метод addRoleToContractor добавляет роль к контрактному исполнителю.
     *
     * @param contractorId идентификатор контрактного исполнителя
     * @param roleId       идентификатор роли
     */
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

    /**
     * Метод deactivateRoleForContractor деактивирует роль у контрактного исполнителя.
     *
     * @param contractorId идентификатор контрактного исполнителя
     * @param roleId       идентификатор роли
     */
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
        existingContractor.setModifyDate(LocalDate.now());
        existingContractor.setModifyUserId(DEFAULT_USER_ID);
        return contractorRepository.save(existingContractor);
    }
}
