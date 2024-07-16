package com.alkl1m.deal.service.impl;

import com.alkl1m.deal.domain.entity.Contractor;
import com.alkl1m.deal.domain.entity.Deal;
import com.alkl1m.deal.domain.entity.Role;
import com.alkl1m.deal.domain.entity.Status;
import com.alkl1m.deal.domain.entity.Type;
import com.alkl1m.deal.domain.exception.ContractorNotFoundException;
import com.alkl1m.deal.domain.exception.DealNotFoundException;
import com.alkl1m.deal.domain.exception.StatusNotFoundException;
import com.alkl1m.deal.domain.exception.TypeNotFoundException;
import com.alkl1m.deal.repository.ContractorRepository;
import com.alkl1m.deal.repository.DealRepository;
import com.alkl1m.deal.repository.RoleRepository;
import com.alkl1m.deal.repository.StatusRepository;
import com.alkl1m.deal.repository.TypeRepository;
import com.alkl1m.deal.repository.spec.DealSpecifications;
import com.alkl1m.deal.service.DealService;
import com.alkl1m.deal.web.payload.ChangeStatusPayload;
import com.alkl1m.deal.web.payload.ContractorDto;
import com.alkl1m.deal.web.payload.DealDto;
import com.alkl1m.deal.web.payload.DealFiltersPayload;
import com.alkl1m.deal.web.payload.NewDealPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private final DealRepository dealRepository;
    private final ContractorRepository contractorRepository;
    private final TypeRepository typeRepository;
    private final StatusRepository statusRepository;
    private static final String DEFAULT_USER_ID = "1";


    @Override
    public List<Deal> getDealsByParameters(DealFiltersPayload payload) {
        Specification<Deal> spec = DealSpecifications.getDealByParameters(payload);

        List<Deal> dealsPage = dealRepository.findAll(spec);

        return dealsPage;
    }

    @Override
    public DealDto findById(UUID id) {
        Deal deal = dealRepository.findById(id).orElseThrow(() -> new ContractorNotFoundException("Contractor not found exception"));
        List<Contractor> contractors = contractorRepository.findActiveContractorsByDealIdAndIsActiveIsTrue(id);
        List<ContractorDto> contractorDtos = new ArrayList<>();
        for (Contractor contractor : contractors) {
            contractorDtos.add(ContractorDto.from(contractor));
        }
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
            }
        } else {
            deal = createNewDeal(payload);
        }
        return DealDto.from(deal, new ArrayList<>());
    }

    @Override
    @Transactional
    public DealDto changeStatus(ChangeStatusPayload payload) {
        Deal deal = dealRepository.findById(payload.dealId()).orElseThrow(() -> new DealNotFoundException("Deal not found"));
        Status status = statusRepository.findById(payload.statusId()).orElseThrow(() -> new StatusNotFoundException("Status not found"));
        deal.setStatus(status);
        return DealDto.from(deal, new ArrayList<>());
    }

    private Deal createNewDeal(NewDealPayload payload) {
        Type type = typeRepository.findById(payload.typeId()).orElseThrow(() -> new TypeNotFoundException("Type not found"));
        Status status = statusRepository.findById("DRAFT").orElseThrow(() -> new StatusNotFoundException("Status not found"));
        Deal deal = NewDealPayload.toDeal(payload, type, status, DEFAULT_USER_ID);
        deal.setId(UUID.randomUUID());

        return dealRepository.save(deal);
    }

    private Deal updateExistingDeal(NewDealPayload payload, Deal existingDeal) {
        Type type = typeRepository.findById(payload.typeId()).orElseThrow(() -> new TypeNotFoundException("Type not found"));

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
