package com.application.user_service.controller;

import com.application.user_service.model.User;
import com.application.user_service.repository.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.org.hamcrest.Matchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.18");

    // converting incoming object to string

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
    void testSaveUsers() throws Exception {
        User user = User.builder()
                .username("testuser1")
                .email("test1@email.com")
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/user")
                        .contentType("application/json")
                        .content(asJsonString(user))
                        .accept("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())

                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }


    @Test
    void testGetUsers() throws Exception {
        createUser("test6");
        // Act & Assert: Perform GET request and validate response
        mockMvc.perform(get("/user")
                        .contentType("application/json")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].username").isNotEmpty())
                .andExpect(jsonPath("$[0].email").isNotEmpty());
    }

    @Test
    void testGetUsersById() throws Exception {
        createUser("test5");
        int userId = userRepository.findAll().get(0).getId();
        mockMvc.perform(get("/user/{id}", userId)
                        .contentType("application/json")
                        .accept("application/json")
                ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").isNotEmpty())
                .andExpect(jsonPath("$.email").isNotEmpty());
    }

    @Test
    void testUpdateUsersById() throws Exception {
        createUser("test4");
        int userId = userRepository.findAll().get(0).getId();
        User details = new User();
        details.setUsername("test2");
        details.setEmail("test2@email.com");

        mockMvc.perform(put("/user/{id}", userId)
                        .contentType("application/json")
                        .content(asJsonString(details)))
                .andExpect(status().isOk())
                .andExpect(content().string("User details updated successfully"));
    }

    @Test
    void testDeleteUserById() throws Exception{
        createUser("test3");
        int userId = userRepository.findAll().get(0).getId();
        mockMvc.perform(delete("/user/{id}",userId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string("Record with the mentioned id deleted successfully"));
    }


    public void createUser(String name) {
        User user = User.builder()
                .username(name)
                .email("test2@email.com")
                .build();
        userRepository.save(user);
    }

}