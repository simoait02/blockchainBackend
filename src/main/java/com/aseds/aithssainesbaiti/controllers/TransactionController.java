package com.aseds.aithssainesbaiti.controllers;

import com.aseds.aithssainesbaiti.domain.Transaction;
import com.aseds.aithssainesbaiti.services.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return transactionService.addTransaction(transaction.getSender(),transaction.getRecipient(),transaction.getAmount());
    }

    @GetMapping
    public List<Transaction> getPendingTransactions() {
        return transactionService.getPendingTransactions();
    }
}
