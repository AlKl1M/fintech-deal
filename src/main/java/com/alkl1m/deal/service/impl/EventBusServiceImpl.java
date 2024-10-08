package com.alkl1m.deal.service.impl;

import com.alkl1m.deal.domain.entity.ContractorOutbox;
import com.alkl1m.deal.rabbitmq.MainBorrowerProducer;
import com.alkl1m.deal.service.EventBusService;
import com.alkl1m.deal.web.payload.MainBorrowerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Реализация сервиса для работы с шиной событий.
 */
@Service
@RequiredArgsConstructor
public class EventBusServiceImpl implements EventBusService {

    private final MainBorrowerProducer producer;

    /**
     * Метод publishContractor публикует информацию о контрагенте через шину событий.
     *
     * @param contractorOutbox объект, содержащий информацию о контрагенте
     * @return ResponseEntity<Void> с результатом публикации
     */
    @Override
    public void publishContractor(ContractorOutbox contractorOutbox) {
        MainBorrowerRequest request = new MainBorrowerRequest(
                contractorOutbox.getContractorId(),
                contractorOutbox.isMain());
        producer.sendMessage(request);
    }
}
