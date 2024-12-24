package com.aseds.aithssainesbaiti.controllers;


import com.aseds.aithssainesbaiti.domain.Transaction;
import com.aseds.aithssainesbaiti.domain.User;
import com.aseds.aithssainesbaiti.services.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("{id}")
    public User getUser(@PathVariable int id) {
        return userService.getUser(id);
    }
    @GetMapping("{id}/sold")
    public double getSold(@PathVariable int id) {
        return UserService.getSold(id);
    }
    @GetMapping("{id}/history")
    public List<Transaction> getUserHistory(@PathVariable int id) {
        return userService.getHistory(id);
    }
}
