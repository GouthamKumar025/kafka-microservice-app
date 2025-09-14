package com.application.product_service.service;

import com.application.product_service.model.Product;
import com.application.product_service.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(int id) {
        return productRepository.findById(id).orElseThrow();
    }

    @Override
    public Product saveProducts(Product product) {
        LOGGER.info("Products are saved successfully");
        return productRepository.save(product);
    }

    @Override
    public String updateProducts(int id, Product details) {
        Product product = productRepository.findById(id).orElseThrow();
        if (!Objects.isNull(details.getName())) {
            product.setName(details.getName());
        }
        if (!Objects.isNull(details.getPrice())) {
            product.setPrice(details.getPrice());
        }
        if (!Objects.isNull(details.getStock())) {
            product.setStock(details.getStock());
        }
        LOGGER.info("Product details: {}", product);
        LOGGER.info("Product with the mentioned id " + id + " is updated successfully");
        productRepository.save(product);
        return "Products updated successfully";
    }

    @Override
    public String deleteProductById(int id) {
        productRepository.deleteById(id);
        LOGGER.info("product with the specified id is deleted successfully");
        return "Product deleted successfully";
    }
}
