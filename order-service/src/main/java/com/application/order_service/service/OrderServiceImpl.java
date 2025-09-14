package com.application.order_service.service;

import com.application.order_service.DTO.ProductDTO;
import com.application.order_service.DTO.UserDTO;
import com.application.order_service.client.ProductServiceClient;
import com.application.order_service.client.UserServiceClient;
import com.application.order_service.model.Order;
import com.application.order_service.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private ProductServiceClient productServiceClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        LOGGER.info("order list : {}", orders);
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(int id) {
        return orderRepository.findById(id).orElseThrow();
    }


    public boolean validateUser(int userId) {
        try {
            UserDTO user = userServiceClient.getUserById(userId);
            LOGGER.info("user details {}", user);
            return user != null;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateProduct(int productId, int quantity) {
        try {
            ProductDTO product = productServiceClient.getProductById(productId);
            if (product != null && product.getStock() >= quantity) {
                int updatedStock = product.getStock() - quantity;
                productServiceClient.updateProductStock(productId, updatedStock);
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }

    //            return product != null && product.getStock() >= quantity;

    public Order saveOrders(Order details) {
        Order order = new Order();
        order.setProductId(details.getProductId());
        order.setUserId(details.getUserId());
        order.setQuantity(details.getQuantity());
        order.setStatus("CONFIRMED");
        LOGGER.info("Orders are saved successfully in the database");
        return orderRepository.save(order);
    }


}
