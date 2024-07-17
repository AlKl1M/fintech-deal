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
import com.alkl1m.deal.web.payload.MainBorrowerMessage;
import com.alkl1m.deal.web.payload.NewDealPayload;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private final DealRepository dealRepository;
    private final TypeRepository typeRepository;
    private final ContractorOutboxService outboxService;
    private final StatusRepository statusRepository;
    private static final String DEFAULT_USER_ID = "1";


    @Override
    public List<DealDto> getDealsByParameters(DealFiltersPayload payload) {
        Specification<Deal> spec = DealSpecifications.getDealByParameters(payload);
        List<Deal> deals = dealRepository.findAll(spec);

        return deals.stream()
                .map(deal -> {
                    Set<ContractorDto> contractors = deal.getContractors().stream()
                            .map(ContractorDto::from)
                            .collect(Collectors.toSet());
                    return DealDto.from(deal, contractors);
                })
                .toList();
    }

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

    @Override
    @Transactional
    public DealDto saveOrUpdate(NewDealPayload payload) {
        Deal deal = new Deal();
        if (payload.id() != null && dealRepository.existsById(payload.id())) {
            Deal existingDeal = dealRepository.findById(payload.id()).orElse(null);
            if (existingDeal != null) {
                deal = updateExistingDeal(payload, existingDeal);
                Set<ContractorDto> contractorDtos = deal.getContractors()
                        .stream()
                        .map(ContractorDto::from)
                        .collect(Collectors.toSet());
                return DealDto.from(deal, contractorDtos);
            }
        }
        deal = createNewDeal(payload);
        return DealDto.from(deal, new HashSet<>());
    }

    @Override
    @Transactional
    public void changeStatus(ChangeStatusPayload payload) {
        Deal deal = dealRepository.findById(payload.dealId()).orElseThrow(() -> new EntityNotFoundException("Deal not found"));
        Status status = statusRepository.findById(payload.statusId()).orElseThrow(() -> new EntityNotFoundException("Status not found"));

        Contractor mainContractor = deal.getContractors().stream()
                .filter(Contractor::isMain)
                .findFirst()
                .orElse(null);

        if (mainContractor != null) {
            if (deal.getStatus().getId().equals("DRAFT") && status.getId().equals("ACTIVE") &&
                    dealRepository.checkIfDealExists(mainContractor.getContractorId()) <= 1) {
                deal.setStatus(status);
                dealRepository.save(deal);
                outboxService.save(mainContractor);
                outboxService.publishNextBatchToEventBus();
                return;
            }

            if (deal.getStatus().getId().equals("ACTIVE") && status.getId().equals("CLOSED") &&
                    dealRepository.checkIfDealExists(mainContractor.getContractorId()) <= 1) {
                deal.setStatus(status);
                dealRepository.save(deal);
                outboxService.save(mainContractor);
                outboxService.publishNextBatchToEventBus();
                return;
            }
        }

        deal.setStatus(status);
        dealRepository.save(deal);
    }

    private Deal createNewDeal(NewDealPayload payload) {
        Type type = typeRepository.findById(payload.typeId()).orElseThrow(
                () -> new EntityNotFoundException("Type not found")
        );
        Status status = statusRepository.findById("DRAFT").orElseThrow(
                () -> new EntityNotFoundException("Status not found")
        );
        Deal deal = NewDealPayload.toDeal(payload, type, status, DEFAULT_USER_ID);
        deal.setId(UUID.randomUUID());

        return dealRepository.save(deal);
    }

    private Deal updateExistingDeal(NewDealPayload payload, Deal existingDeal) {
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
        existingDeal.setModifyDate(new Date());
        existingDeal.setModifyUserId(DEFAULT_USER_ID);
        return dealRepository.save(existingDeal);
    }

}
