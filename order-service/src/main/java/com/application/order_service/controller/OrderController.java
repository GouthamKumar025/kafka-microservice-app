package com.application.order_service.controller;

import com.application.order_service.events.OrderEventProducer;
import com.application.order_service.model.Order;
import com.application.order_service.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderEventProducer orderEventProducer;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @GetMapping
    private List<Order> getAllOrders() {
        List<Order> order = orderService.getAllOrders();
        LOGGER.info("orders are {}",order);
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    private Order getOrderById(@PathVariable int id) {
        return orderService.getOrderById(id);
    }

    @PostMapping
    private ResponseEntity<String> placeOrder(@RequestBody Order request) {
        boolean flag = orderService.validateUser(request.getUserId());
        LOGGER.info("Boolean value {} ",flag);
        if (!flag) {
            LOGGER.info(String.valueOf(!orderService.validateUser(request.getUserId())));
            LOGGER.info(String.valueOf(request.getUserId()));
            return ResponseEntity.badRequest().body("Invalid user ID");
        }
        if (!orderService.validateProduct(request.getProductId(), request.getQuantity())) {
            LOGGER.info("Invalid product or out of stock");
            return ResponseEntity.badRequest().body("Out of Stock or Invalid Product");
        }
        Order order = orderService.saveOrders(request);
        LOGGER.info("Order details : {}",order);
        orderEventProducer.sendOrderedEvent(order);
        LOGGER.info("Order placed successfully");
        return ResponseEntity.ok("Order placed successfully");
    }
}
