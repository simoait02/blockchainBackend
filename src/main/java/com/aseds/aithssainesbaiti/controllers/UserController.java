package com.aseds.aithssainesbaiti.controllers;

import com.aseds.aithssainesbaiti.domain.Transaction;
import com.aseds.aithssainesbaiti.domain.User;
import com.aseds.aithssainesbaiti.services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }
}
