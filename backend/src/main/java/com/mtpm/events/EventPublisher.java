package com.mtpm.events;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public EventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishAudit(String tenantId, String actorUserId, String action, Map<String, Object> details) {
        var payload = Map.of(
                "type", "AUDIT",
                "tenantId", tenantId,
                "actorUserId", actorUserId,
                "action", action,
                "details", details,
                "ts", System.currentTimeMillis()
        );
        rabbitTemplate.convertAndSend("taskforge.exchange", "audit.created", payload);
    }
}
