package com.alkl1m.deal.service.impl;

import com.alkl1m.deal.domain.entity.Contractor;
import com.alkl1m.deal.domain.entity.Deal;
import com.alkl1m.deal.domain.entity.Status;
import com.alkl1m.deal.domain.entity.Type;
import com.alkl1m.deal.repository.DealRepository;
import com.alkl1m.deal.repository.StatusRepository;
import com.alkl1m.deal.repository.TypeRepository;
import com.alkl1m.deal.repository.spec.DealSpecifications;
import com.alkl1m.deal.service.ContractorOutboxService;
import com.alkl1m.deal.service.DealService;
import com.alkl1m.deal.web.payload.ChangeStatusPayload;
import com.alkl1m.deal.web.payload.ContractorDto;
import com.alkl1m.deal.web.payload.DealDto;
import com.alkl1m.deal.web.payload.DealFiltersPayload;
import com.alkl1m.deal.web.payload.DealsDto;
import com.alkl1m.deal.web.payload.NewDealPayload;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы со сделками.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private final DealRepository dealRepository;
    private final TypeRepository typeRepository;
    private final ContractorOutboxService outboxService;
    private final StatusRepository statusRepository;

    /**
     * Метод getDealsByParameters возвращает список сделок в соответствии с переданными параметрами и пагинацией.
     *
     * @param payload  параметры фильтрации сделок
     * @param pageable информация о пагинации
     * @return объект DealsDto, содержащий список сделок и информацию о пагинации
     */
    @Override
    public DealsDto getDealsByParameters(DealFiltersPayload payload, Pageable pageable, UserDetailsImpl userDetails) {
        Set<String> userRoles = getUserRoles(userDetails);

        validateUserRoles(payload, userRoles);

        Specification<Deal> spec = DealSpecifications.getDealByParameters(payload);
        Page<Deal> deals = dealRepository.findAll(spec, pageable);

        List<DealDto> list = deals.stream()
                .map(deal -> DealDto.from(deal, deal.getContractors().stream()
                        .map(ContractorDto::from)
                        .collect(Collectors.toSet()))
                )
                .toList();

        return new DealsDto(new PageImpl<>(list, deals.getPageable(), deals.getTotalElements()));
    }

    /**
     * Метод findById находит сделку по указанному идентификатору и возвращает ее в виде объекта DealDto.
     *
     * @param id идентификатор сделки
     * @return объект DealDto, представляющий найденную сделку
     */
    @Override
    public DealDto findById(UUID id) {
        Deal deal = dealRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Contractor with id %s not found!", id.toString()))
        );

        Set<ContractorDto> contractorDtos = deal.getContractors()
                .stream()
                .map(ContractorDto::from)
                .collect(Collectors.toSet());

        return DealDto.from(deal, contractorDtos);
    }

    /**
     * Метод saveOrUpdate сохраняет или обновляет данные сделки на основе переданных данных.
     *
     * @param payload данные новой сделки
     * @return объект DealDto, представляющий сохраненную или обновленную сделку
     */
    @Override
    @Transactional
    public DealDto saveOrUpdate(NewDealPayload payload, String userId) {
        if (payload.id() != null && dealRepository.existsById(payload.id())) {
            Deal existingDeal = dealRepository.findById(payload.id()).orElse(null);
            if (existingDeal != null) {
                Deal updatedDeal = updateExistingDeal(payload, existingDeal, userId);
                Set<ContractorDto> contractorDtos = updatedDeal.getContractors()
                        .stream()
                        .map(ContractorDto::from)
                        .collect(Collectors.toSet());
                return DealDto.from(updatedDeal, contractorDtos);
            }
        }
        Deal newDeal = createNewDeal(payload, userId);
        return DealDto.from(newDeal, new HashSet<>());
    }

    /**
     * Метод changeStatus изменяет статус сделки на основе переданных данных.
     *
     * @param payload данные для изменения статуса сделки
     */
    @Override
    @Transactional
    public void changeStatus(ChangeStatusPayload payload, String userId) {
        Deal deal = dealRepository.findById(payload.dealId())
                .orElseThrow(() -> new EntityNotFoundException("Deal not found"));
        Status status = statusRepository.findById(payload.statusId())
                .orElseThrow(() -> new EntityNotFoundException("Status not found"));

        Contractor mainContractor = deal.getContractors().stream()
                .filter(Contractor::isMain)
                .findFirst()
                .orElse(null);

        if (mainContractor != null && dealRepository.checkIfDealExists(mainContractor.getContractorId()) <= 1) {
            if (deal.getStatus().getId().equals("DRAFT") && status.getId().equals("ACTIVE")) {
                processDealStatusChange(deal, status, true, mainContractor.getContractorId(), userId);
            } else if (deal.getStatus().getId().equals("ACTIVE") && status.getId().equals("CLOSED")) {
                processDealStatusChange(deal, status, false, mainContractor.getContractorId(), userId);
            }
        } else {
            deal.setStatus(status);
            dealRepository.save(deal);
        }
    }

    private void processDealStatusChange(Deal deal, Status status, boolean isActiveToActive, String contractorId, String userId) {
        deal.setStatus(status);
        dealRepository.save(deal);
        outboxService.save(isActiveToActive, contractorId);
    }

    private Deal createNewDeal(NewDealPayload payload, String userId) {
        Type type = typeRepository.findById(payload.typeId()).orElseThrow(
                () -> new EntityNotFoundException("Type not found")
        );
        Status status = statusRepository.findById("DRAFT").orElseThrow(
                () -> new EntityNotFoundException("Status not found")
        );
        Deal deal = NewDealPayload.toDeal(payload, type, status, userId);
        deal.setId(UUID.randomUUID());

        return dealRepository.save(deal);
    }

    private Deal updateExistingDeal(NewDealPayload payload, Deal existingDeal, String userId) {
        Type type = typeRepository.findById(payload.typeId()).orElseThrow(
                () -> new EntityNotFoundException("Type not found")
        );

        existingDeal.setDescription(payload.description());
        existingDeal.setAgreementNumber(payload.agreementNumber());
        existingDeal.setAgreementDate(payload.agreementDate());
        existingDeal.setAgreementStartDt(payload.agreementStartDt());
        existingDeal.setAvailabilityDate(payload.availabilityDate());
        existingDeal.setType(type);
        existingDeal.setSum(payload.sum());
        existingDeal.setCloseDt(payload.closeDt());
        existingDeal.setModifyDate(LocalDate.now());
        existingDeal.setModifyUserId(userId);
        return dealRepository.save(existingDeal);
    }

    private Set<String> getUserRoles(UserDetailsImpl userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    private void validateUserRoles(DealFiltersPayload payload, Set<String> userRoles) {
        String expectedType = null;

        if (userRoles.contains("CREDIT_USER")) {
            expectedType = "CREDIT";
        } else if (userRoles.contains("OVERDRAFT_USER")) {
            expectedType = "OVERDRAFT";
        }

        if (expectedType != null) {
            if (payload.type().size() != 1 || !payload.type().get(0).getId().equals(expectedType)) {
                throw new AuthenticationServiceException("Пользователь с такой ролью не может просматривать эти данные.");
            }
        }
    }

}
