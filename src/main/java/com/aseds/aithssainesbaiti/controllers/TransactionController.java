package com.aseds.aithssainesbaiti.controllers;

import com.aseds.aithssainesbaiti.domain.Transaction;
import com.aseds.aithssainesbaiti.services.TransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for transaction operations.
 * This controller is designed as final and not for extension.
 * All methods are self-contained and provide complete transaction functionality.
 */
@RestController
@RequestMapping("/api/transactions")
public final class TransactionController {
    /**
     * Service for transaction operations.
     */
    private final TransactionService transactionService;

    /**
     * Constructor for TransactionController.
     *
     * @param service the transaction service to be injected
     */
    public TransactionController(final TransactionService service) {
        this.transactionService = service;
    }

    /**
     * Creates a new transaction.
     *
     * @param transaction the transaction to be created
     * @return the created transaction
     */
    @PostMapping
    public Transaction createTransaction(@RequestBody final Transaction transaction) {
        return transactionService.addTransaction(
                transaction.getSenderId(),
                transaction.getRecipientId(),
                transaction.getAmount()
        );
    }

    /**
     * Retrieves all pending transactions.
     *
     * @return List of pending transactions
     */
    @GetMapping
    public List<Transaction> getPendingTransactions() {
        return TransactionService.getPendingTransactions();
    }
}
