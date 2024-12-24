package com.aseds.aithssainesbaiti.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class Block {
    private final int index;
    private final String previousHash;
    private final String hash;
    private final List<Transaction> transactions;
    private final long timestamp;
    private final int proof;

    public Block(int index, String previousHash, List<Transaction> transactions, int proof) {
        this.index = index;
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.timestamp = System.currentTimeMillis();
        this.proof = proof;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        try {
            StringBuilder transactionData = new StringBuilder();
            for (Transaction transaction : transactions) {
                transactionData.append(new ObjectMapper().writeValueAsString(transaction));
            }
            String input = index + previousHash + timestamp + transactionData + proof;
            return HashUtils.hash(input);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating hash", e);
        }
    }

    public int getIndex() { return index; }
    public String getPreviousHash() { return previousHash; }
    public String getHash() { return hash; }
    public List<Transaction> getTransactions() { return transactions; }
    public long getTimestamp() { return timestamp; }
    public int getProof() { return proof; }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing Block to JSON", e);
        }
    }
}
