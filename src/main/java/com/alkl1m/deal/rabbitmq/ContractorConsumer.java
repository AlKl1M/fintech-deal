package com.alkl1m.deal.rabbitmq;

import com.alkl1m.deal.config.MQConfiguration;
import com.alkl1m.deal.domain.entity.Contractor;
import com.alkl1m.deal.repository.ContractorRepository;
import com.alkl1m.deal.web.payload.UpdateContractorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ContractorConsumer {
    private final ContractorRepository contractorRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.retryCount}")
    private Long retryCount;

    @RabbitListener(id = MQConfiguration.DEALS_CONTRACTOR_NEW_DATA_QUEUE, queues = MQConfiguration.DEALS_CONTRACTOR_NEW_DATA_QUEUE)
    public void receiveMessage(UpdateContractorMessage msg, @Header(required = false, name = "x-death") Map<String, ?> xDeath) {
        if (checkRetryCount(xDeath)) {
            sendToUndelivered(msg);
            return;
        }
        try {
            contractorRepository.findByContractorId(msg.id())
                    .filter(existingContractor -> isUpdateRequired(existingContractor, msg))
                    .ifPresent(existingContractor -> updateAndSave(existingContractor, msg));
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

    private boolean checkRetryCount(Map<String, ?> xDeath) {
        return xDeath != null && !xDeath.isEmpty() && ((Long) xDeath.get("count")) >= retryCount;
    }

    private void sendToUndelivered(UpdateContractorMessage msg) {
        rabbitTemplate.convertAndSend(MQConfiguration.DEALS_CONTRACTOR_UNDELIVERED_QUEUE, msg);
    }

    private boolean isUpdateRequired(Contractor contractor, UpdateContractorMessage msg) {
        ZonedDateTime modifyDateTime = ZonedDateTime.parse(msg.modifyDate(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
        return contractor.getModifyDate() == null || modifyDateTime.isAfter(contractor.getModifyDate());
    }

    private void updateAndSave(Contractor contractor, UpdateContractorMessage msg) {
        contractor.setName(msg.name());
        contractor.setInn(msg.inn());
        contractor.setModifyDate(ZonedDateTime.parse(msg.modifyDate(), DateTimeFormatter.ISO_ZONED_DATE_TIME));
        contractor.setModifyUserId(msg.modifyUser());
        contractorRepository.save(contractor);
    }
}
