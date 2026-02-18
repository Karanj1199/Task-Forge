package com.mtpm.events;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    DirectExchange taskforgeExchange() {
        return new DirectExchange("taskforge.exchange");
    }

    @Bean
    Queue auditQueue() {
        return new Queue("taskforge.audit.queue", true);
    }

    @Bean
    Binding auditBinding(Queue auditQueue, DirectExchange taskforgeExchange) {
        return BindingBuilder.bind(auditQueue).to(taskforgeExchange).with("audit.created");
    }
}
