package com.application.product_service.controller;

import com.application.product_service.model.Product;
import com.application.product_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.18");


    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final String jsonContent = objectMapper.writeValueAsString(obj);
            return jsonContent;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSaveProduct() throws Exception {
        Product product = Product.builder()
                .name("testproduct")
                .price(50.25)
                .stock(10)
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/product")
                        .contentType("application/json")
                        .content(asJsonString(product))
                        .accept("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }


    @Test
    void testGetProduct() throws Exception {
        createProduct("test6");
        // Act & Assert: Perform GET request and validate response
        mockMvc.perform(get("/product")
                        .contentType("application/json")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").isNotEmpty())
                .andExpect(jsonPath("$[0].stock").isNotEmpty())
                .andExpect(jsonPath("$[0].price").isNotEmpty());
    }

    @Test
    void testGetProductById() throws Exception {
        createProduct("test5");
        int productId = productRepository.findAll().get(0).getId();
        mockMvc.perform(get("/product/{productId}", productId)
                        .contentType("application/json")
                        .accept("application/json")
                ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.price").isNotEmpty())
                .andExpect(jsonPath("$.stock").isNotEmpty());
    }

    @Test
    void testUpdateProductById() throws Exception {
        createProduct("test4");
        int productId = productRepository.findAll().get(0).getId();
        Product details = new Product();
        details.setName("testproduct2");
        details.setPrice(85.32);
        details.setStock(25);

        mockMvc.perform(put("/product/{id}", productId)
                        .contentType("application/json")
                        .content(asJsonString(details)))
                .andExpect(status().isOk())
                .andExpect(content().string("Products updated successfully"));
    }

    @Test
    void testDeleteUserById() throws Exception {
        createProduct("product3");
        int productId = productRepository.findAll().get(0).getId();
        mockMvc.perform(delete("/product/{id}", productId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully"));
    }


    public void createProduct(String name) {
        Product product = Product.builder()
                .name(name)
                .price(50.23)
                .stock(10)
                .build();
        productRepository.save(product);
    }

}