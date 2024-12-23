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
    public Transaction addTransaction(int sender, int recipient, double amount) {
        Transaction transaction = new Transaction(sender, recipient, amount);
        pendingTransactions.add(transaction);
        return transaction;
    }

    public static List<Transaction> getPendingTransactions() {
        return new ArrayList<>(pendingTransactions);
    }
    public static void removeTransaction(Transaction transaction) {
        pendingTransactions.remove(transaction);
    }

    public void clearPendingTransactions() {
        pendingTransactions.clear();
    }

    public List<Transaction> prepareTransactionsForMining(String minerAddress) {
        List<Transaction> transactionsForMining = new ArrayList<>(pendingTransactions);
        transactionsForMining.add(new Transaction(0, 0, 50.0));
        return transactionsForMining;
    }
}
