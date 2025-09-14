package com.application.notification_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationConsumer.class);

    @KafkaListener(topics = "ordercreated", groupId = "notification-group")
    public void consumeMessage(String message) {
        LOGGER.info("Message received: " + message);
    }
}
