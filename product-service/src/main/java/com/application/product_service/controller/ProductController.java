package com.application.product_service.controller;

import com.application.product_service.model.Product;
import com.application.product_service.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @GetMapping
    private List<Product> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/{id}")
    private Product getProductById(@PathVariable int id) {
        return productService.getProductById(id);
    }


    @PostMapping
    private Product saveProducts(@RequestBody Product product) {
        LOGGER.info("Products are saved successfully");
        return productService.saveProducts(product);
    }

    @PutMapping("/{id}")
    private String updateProducts(@PathVariable int id, @RequestBody Product details) {
        productService.updateProducts(id, details);
        LOGGER.info("Product with the mentioned id " + id + " is updated successfully");
        LOGGER.info("Product details: {}", details);
        return "Products updated successfully";
    }

    @DeleteMapping("/{id}")
    private String deleteProductById(@PathVariable int id) {
        productService.deleteProductById(id);
        LOGGER.info("Product with mentioned id " + id + "is deleted from the database");
        return "Product deleted successfully";
    }

}
