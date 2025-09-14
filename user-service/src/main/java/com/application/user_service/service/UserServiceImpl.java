package com.application.user_service.service;

import com.application.user_service.model.User;
import com.application.user_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public List<User> getUserDetails() {
        return userRepository.findAll();
    }

    @Override
    public User saveUserDetails(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserDetailsById(int id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Override
    public String updateUserDetails(int id, User details) {
        User user = userRepository.findById(id).orElseThrow();
        if (!Objects.isNull(details.getUsername())) {
            user.setUsername(details.getUsername());
        }
        if (!Objects.isNull(details.getEmail())) {
            user.setEmail(details.getEmail());
        }
        userRepository.save(user);
        LOGGER.info("User record successfully updated");
        return "Record updated successfully";

    }

    @Override
    public String deleteByUserId(int id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            LOGGER.info("User record with the mentioned id deleted successfully");
            return "Records deleted successfully";
        }
        return "No records exists there";
    }
}
