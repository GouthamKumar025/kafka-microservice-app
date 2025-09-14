package com.application.order_service.events;

import com.application.order_service.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventProducer {

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public void sendOrderedEvent(Order order){

        String message = String.format(
                "{\"orderId\": %d, \"userId\": %d, \"productId\": %d, \"quantity\": %d, \"status\": \"%s\"}",
                order.getId(), order.getUserId(), order.getProductId(), order.getQuantity(), order.getStatus()
        );

        kafkaTemplate.send("ordercreated", message);

    }

}
