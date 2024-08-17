package com.alkl1m.deal.rabbitmq;

import com.alkl1m.deal.config.MQConfiguration;
import com.alkl1m.deal.domain.entity.Contractor;
import com.alkl1m.deal.repository.ContractorRepository;
import com.alkl1m.deal.web.payload.UpdateContractorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class ContractorConsumer {
    private final ContractorRepository contractorRepository;

    @RabbitListener(id = MQConfiguration.DEAL_CONTRACTOR_QUEUE, queues = MQConfiguration.DEAL_CONTRACTOR_QUEUE)
    public void receiveMessage(UpdateContractorMessage msg) {
        try {
            Optional<Contractor> contractorOpt = contractorRepository.findByContractorId(msg.id());
            contractorOpt.ifPresent(existingContractor -> {
                ZonedDateTime modifyDateTime = ZonedDateTime.parse(msg.modifyDate(), DateTimeFormatter.ISO_ZONED_DATE_TIME);

                if (existingContractor.getModifyDate() == null || modifyDateTime.isAfter(existingContractor.getModifyDate())) {
                    updateContractor(existingContractor, msg, modifyDateTime);
                    contractorRepository.save(existingContractor);
                }
            });
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    private void updateContractor(Contractor contractor, UpdateContractorMessage msg, ZonedDateTime modifyDateTime) {
        contractor.setName(msg.name());
        contractor.setInn(msg.inn());
        contractor.setModifyDate(modifyDateTime);
        contractor.setModifyUserId(msg.modifyUser());
    }
}
