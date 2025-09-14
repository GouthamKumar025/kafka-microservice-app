package com.application.product_service.service;

import com.application.product_service.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> getProducts();

    Product getProductById(int id);

    Product saveProducts(Product product);

    String updateProducts(int id, Product details);

    String deleteProductById(int id);
}
