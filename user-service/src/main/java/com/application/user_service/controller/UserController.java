package com.application.user_service.controller;

import com.application.user_service.model.User;
import com.application.user_service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    private List<User> getUserDetails(){
        LOGGER.info("Response of all the user details got successfully");
        List<User> res= userService.getUserDetails();
        LOGGER.info("result : {}",res);
        return res;
    }

    @GetMapping("/{id}")
    private User getUserDetailsById(@PathVariable int id){
        LOGGER.info("User details got by Id");
        return userService.getUserDetailsById(id);
    }

    @PostMapping
    private User saveUserDetails(@RequestBody User user){
        LOGGER.info("User details saved to the database");
        return userService.saveUserDetails(user);
    }

    @PutMapping("/{id}")
    private String updateUserDetails(@PathVariable int id, @RequestBody User details){
        userService.updateUserDetails(id,details);
        LOGGER.info("User details are updated with the mentioned id: ", + id);
        return "User details updated successfully";
    }

    @DeleteMapping("/{id}")
    private String deleteByUserId(@PathVariable int id){
        userService.deleteByUserId(id);
        LOGGER.info("User mentioned with the given id has been deleted successfully from the DB");
        return "Record with the mentioned id deleted successfully";
    }



}
