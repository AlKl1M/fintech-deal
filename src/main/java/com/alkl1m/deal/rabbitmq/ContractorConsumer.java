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

/**
 * Класс-слушатель для обработки сообщений об обновлении контрагентов.
 *
 * @author alkl1m
 */
@Component
@RequiredArgsConstructor
public class ContractorConsumer {

    private final ContractorRepository contractorRepository;
    private final RabbitTemplate rabbitTemplate;

    /**
     * Максимальное количество попыток повторной отправки сообщения.
     */
    @Value("${spring.rabbitmq.retryCount}")
    private Long retryCount;

    /**
     * Метод для получения сообщений из очереди обновления подрядчиков.
     * Проверяет количество повторных отправок и может выбрасывать исключение
     * AmqpRejectAndDontRequeueException в случае, если какое-либо другое исключение
     * будет выброшено во время обработки, чтобы перекинуть сообщение в очередь
     * мертвых сообщений.
     *
     * @param msg    сообщение об обновлении контрагента.
     * @param xDeath заголовок, содержащий информацию о количестве попыток повторной отправки.
     */
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

    /**
     * Проверяет количество попыток повторной отправки сообщения.
     *
     * @param xDeath заголовок с информацией о количестве попыток.
     * @return true, если количество попыток превышает максимально допустимое; иначе false.
     */
    private boolean checkRetryCount(Map<String, ?> xDeath) {
        return xDeath != null && !xDeath.isEmpty() && ((Long) xDeath.get("count")) >= retryCount;
    }

    /**
     * Отправляет сообщение в очередь недоставленных, если количество попыток превышает лимит.
     *
     * @param msg сообщение об обновлении контрагента.
     */
    private void sendToUndelivered(UpdateContractorMessage msg) {
        rabbitTemplate.convertAndSend(MQConfiguration.DEALS_CONTRACTOR_UNDELIVERED_QUEUE, msg);
    }

    /**
     * Проверяет, требуется ли обновление данных контрагентов.
     *
     * @param contractor существующий контрагент.
     * @param msg        сообщение об обновлении контрагента.
     * @return true, если обновление требуется; иначе false.
     */
    private boolean isUpdateRequired(Contractor contractor, UpdateContractorMessage msg) {
        ZonedDateTime modifyDateTime = ZonedDateTime.parse(msg.modifyDate(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
        return contractor.getModifyDate() == null || modifyDateTime.isAfter(contractor.getModifyDate());
    }

    /**
     * Обновляет данные контрагента и сохраняет их в базе данных.
     *
     * @param contractor существующий контрагент.
     * @param msg        сообщение об обновлении контрагента.
     */
    private void updateAndSave(Contractor contractor, UpdateContractorMessage msg) {
        contractor.setName(msg.name());
        contractor.setInn(msg.inn());
        contractor.setModifyDate(ZonedDateTime.parse(msg.modifyDate(), DateTimeFormatter.ISO_ZONED_DATE_TIME));
        contractor.setModifyUserId(msg.modifyUser());
        contractorRepository.save(contractor);
    }
}
