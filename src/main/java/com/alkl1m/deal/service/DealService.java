package com.alkl1m.deal.service;

import com.alkl1m.deal.domain.entity.Deal;
import com.alkl1m.deal.web.payload.ChangeStatusPayload;
import com.alkl1m.deal.web.payload.ContractorDto;
import com.alkl1m.deal.web.payload.DealDto;
import com.alkl1m.deal.web.payload.DealFiltersPayload;
import com.alkl1m.deal.web.payload.NewDealPayload;

import java.util.List;
import java.util.UUID;

public interface DealService {

    List<Deal> getDealsByParameters(DealFiltersPayload payload);

    DealDto findById(UUID id);

    DealDto saveOrUpdate(NewDealPayload payload);

    DealDto changeStatus(ChangeStatusPayload payload);


}
