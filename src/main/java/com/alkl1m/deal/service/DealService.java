package com.alkl1m.deal.service;

import com.alkl1m.deal.service.impl.UserDetailsImpl;
import com.alkl1m.deal.web.payload.ChangeStatusPayload;
import com.alkl1m.deal.web.payload.DealDto;
import com.alkl1m.deal.web.payload.DealFiltersPayload;
import com.alkl1m.deal.web.payload.DealsDto;
import com.alkl1m.deal.web.payload.NewDealPayload;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface DealService {

    DealsDto getDealsByParameters(DealFiltersPayload payload, Pageable pageable, UserDetailsImpl userDetails);

    DealDto findById(UUID id);

    DealDto saveOrUpdate(NewDealPayload payload, String userId);

    void changeStatus(ChangeStatusPayload payload, String userId);

}
