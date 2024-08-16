package com.alkl1m.deal.rabbitmq;

import com.alkl1m.deal.config.MQConfiguration;
import com.alkl1m.deal.domain.entity.Contractor;
import com.alkl1m.deal.repository.ContractorRepository;
import com.alkl1m.deal.web.payload.UpdateContractorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class ContractorConsumer {
    private final ContractorRepository contractorRepository;

    @RabbitListener(queues = MQConfiguration.DEAL_CONTRACTOR_QUEUE)
    public void receiveMessage(UpdateContractorMessage msg) {
        try {
            Optional<Contractor> contractorOpt = contractorRepository.findByContractorId(msg.id());
            contractorOpt.ifPresent(existingContractor -> {
                LocalDate modifyDate = LocalDate.parse(msg.modifyDate());
                if (existingContractor.getModifyDate() == null || modifyDate.isBefore(existingContractor.getModifyDate())) {
                    updateContractor(existingContractor, msg, modifyDate);
                    contractorRepository.save(existingContractor);
                }
            });
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    private void updateContractor(Contractor contractor, UpdateContractorMessage msg, LocalDate modifyDate) {
        contractor.setName(msg.name());
        contractor.setInn(msg.inn());
        contractor.setModifyDate(modifyDate);
        contractor.setModifyUserId(msg.modifyUser());
    }
}
