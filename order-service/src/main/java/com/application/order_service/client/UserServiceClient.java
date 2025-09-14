package com.application.order_service.client;

import com.application.order_service.DTO.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    public UserDTO getUserById(int userId){
        String url = "http://localhost:8081/user/" + userId;
        return restTemplate.getForObject(url, UserDTO.class);
    }
}
