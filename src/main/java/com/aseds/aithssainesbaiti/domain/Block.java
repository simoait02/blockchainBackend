package com.aseds.aithssainesbaiti.domain;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Block {
    private final int index;
    private final String previousHash;
    private final String hash;
    private final Transaction data; // Store the Transaction object directly
    private final long timestamp;
    private final int proof;

    public Block(int index, String previousHash, Transaction data, int proof) {
        this.index = index;
        this.previousHash = previousHash;
        this.data = data; // Store the Transaction object (not a JSON string)
        this.timestamp = System.currentTimeMillis();
        this.proof = proof;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        try {
            String input = index + previousHash + timestamp + new ObjectMapper().writeValueAsString(data) + proof;
            return HashUtils.hash(input);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating hash", e);
        }
    }

    // Getters
    public int getIndex() { return index; }
    public String getPreviousHash() { return previousHash; }
    public String getHash() { return hash; }
    public Transaction getData() { return data; }
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
