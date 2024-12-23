package com.aseds.aithssainesbaiti.services;
import com.aseds.aithssainesbaiti.domain.Transaction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private static List<Transaction> pendingTransactions;

    public TransactionService() {
        pendingTransactions = new ArrayList<>();
    }
    public Transaction addTransaction(String sender, String recipient, double amount) {
        Transaction transaction = new Transaction(sender, recipient, amount);
        pendingTransactions.add(transaction);
        return transaction;
    }

    public static List<Transaction> getPendingTransactions() {
        return new ArrayList<>(pendingTransactions);
    }

    public void clearPendingTransactions() {
        pendingTransactions.clear();
    }

    public List<Transaction> prepareTransactionsForMining(String minerAddress) {
        List<Transaction> transactionsForMining = new ArrayList<>(pendingTransactions);
        transactionsForMining.add(new Transaction("System", minerAddress, 50.0)); // Example reward
        return transactionsForMining;
    }
}
