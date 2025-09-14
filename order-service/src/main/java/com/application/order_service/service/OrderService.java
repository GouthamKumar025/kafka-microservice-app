package com.application.order_service.service;


import com.application.order_service.model.Order;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();

    Order getOrderById(int id);

    Order saveOrders(Order order);

    boolean validateUser(int userId);

    boolean validateProduct(int productId, int quantity);
}
