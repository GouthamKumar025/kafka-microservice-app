package com.application.order_service.controller;

import com.application.order_service.DTO.ProductDTO;
import com.application.order_service.DTO.UserDTO;
import com.application.order_service.client.ProductServiceClient;
import com.application.order_service.client.UserServiceClient;
import com.application.order_service.events.OrderEventProducer;
import com.application.order_service.model.Order;
import com.application.order_service.repository.OrderRepository;
import com.application.order_service.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class OrderControllerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderControllerTest.class);


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @MockitoBean
    private ProductServiceClient productServiceClient;

    @MockitoBean
    private UserServiceClient userServiceClient;

    @Container
    @ServiceConnection
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));


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
    void testSaveOrder() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("abc");
        userDTO.setEmail("abc@email.com");

        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("test1");
        productDTO.setPrice(25.60);
        productDTO.setStock(12);

        when(userServiceClient.getUserById(anyInt())).thenReturn(userDTO);
        when(productServiceClient.getProductById(anyInt())).thenReturn(productDTO);
        Order order = Order
                .builder()
                .userId(1)
                .productId(2)
                .quantity(4)
                .status("CONFIRMED")
                .build();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/order")
                        .contentType("application/json")
                        .content(asJsonString(order))
                        .accept("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        int id = orderRepository.findAll().get(0).getId();
        Assertions.assertNotNull(id);
        System.out.println("Final result: " + result.getResponse().getContentAsString());
    }
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/order")
//                        .contentType("application/json")
//                        .content(asJsonString(order))
//                        .accept("application/json"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());


    @Test
    void testGetOrders() throws Exception {
        createOrder();
        // Act & Assert: Perform GET request and validate response
        mockMvc.perform(get("/order")
                        .contentType("application/json")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].userId").isNotEmpty())
                .andExpect(jsonPath("$[0].productId").isNotEmpty())
                .andExpect(jsonPath("$[0].quantity").isNotEmpty())
                .andExpect(jsonPath("$[0].status").isNotEmpty());
    }

    @Test
    void testGetOrderById() throws Exception {
        createOrder();
        int orderId = orderRepository.findAll().get(0).getId();
        mockMvc.perform(get("/order/{orderId}", orderId)
                        .contentType("application/json")
                        .accept("application/json")
                ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.productId").isNotEmpty())
                .andExpect(jsonPath("$.quantity").isNotEmpty())
                .andExpect(jsonPath("$.status").isNotEmpty());
    }

    public void createOrder() {
        Order order = Order
                .builder()
                .userId(1)
                .productId(2)
                .quantity(4)
                .status("CONFIRMED")
                .build();
        orderRepository.save(order);
    }
    @KafkaListener(topics = "ordercreated", groupId = "notification-group")
    public void consumeMessage(String message) {
        LOGGER.info("Message received: " + message);
    }
}