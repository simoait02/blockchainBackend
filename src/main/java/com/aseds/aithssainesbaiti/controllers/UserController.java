package com.aseds.aithssainesbaiti.controllers;


import com.aseds.aithssainesbaiti.domain.Transaction;
import com.aseds.aithssainesbaiti.domain.User;
import com.aseds.aithssainesbaiti.services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

/**
 * REST controller for user operations.
 * This controller is designed as final and not for extension.
 * All methods are self-contained and provide complete user functionality.
 */

@RestController
@RequestMapping("/api/users")
public final class UserController {
    /**
     * Service for user operations.
     */
    private final UserService userService;

    /**
     * Constructor for UserController.
     * @param service the user service to be injected
     */
    public UserController(final UserService service) {
        this.userService = service;
    }

    /**
     * Creates a new user.
     * @param user the user to be created
     * @return the created user
     */
    @PostMapping
    public User addUser(final @RequestBody User user) {
        return userService.addUser(user);
    }

    /**
     * Retrieves a user by ID.
     * @param id the user ID
     * @return the user with the specified ID
     */
    @GetMapping("{id}")
    public User getUser(final @PathVariable int id) {
        return userService.getUser(id);
    }

    /**
     * Retrieves the sold amount for a user.
     * @param id the user ID
     * @return the sold amount for the user with the specified ID
     */
    @GetMapping("{id}/sold")
    public double getSold(final @PathVariable int id) {
        return UserService.getSold(id);
    }

    /**
     * Retrieves the transaction history for a user.
     * @param id the user ID
     * @return the transaction history for the user with the specified ID
     */
    @GetMapping("{id}/history")
    public List<Transaction> getUserHistory(final @PathVariable int id) {
        return userService.getHistory(id);
    }
}
