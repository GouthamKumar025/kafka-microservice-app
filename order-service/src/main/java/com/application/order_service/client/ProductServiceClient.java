package com.application.order_service.client;

import com.application.order_service.DTO.ProductDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductServiceClient {
    @Autowired
    private RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceClient.class);

    public ProductDTO getProductById(int productId) {
        String url = "http://localhost:8082/product/" + productId;
        return restTemplate.getForObject(url, ProductDTO.class);
    }

    public void updateProductStock(int productId, int newStock) {
        String url = "http://localhost:8082/product/" + productId;
        ProductDTO updatedProduct = getProductById(productId);
        logger.info("updated product: {}", updatedProduct);
        if (updatedProduct != null) {
            updatedProduct.setStock(newStock);
            restTemplate.put(url, updatedProduct);
        }
    }
}
