package com.application.user_service.service;


import com.application.user_service.model.User;

import java.util.List;

public interface UserService {
    List<User> getUserDetails();

    User saveUserDetails(User user);

    User getUserDetailsById(int id);

    String updateUserDetails(int id, User details);

    String deleteByUserId(int id);
}
