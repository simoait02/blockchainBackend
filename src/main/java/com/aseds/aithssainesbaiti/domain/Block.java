package com.aseds.aithssainesbaiti.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.List;

/**
 * Represents a block in the blockchain.
 * This class is designed as final and not for extension.
 * Each block contains an index, hash, previous hash, transactions, timestamp, and proof.
 */
@Getter
public final class Block {

    /**
     * The index position of this block in the blockchain.
     */
    private final int index;

    /**
     * The hash of the previous block in the chain.
     */
    private final String previousHash;

    /**
     * The calculated hash of this block.
     */
    private final String hash;

    /**
     * The list of transactions included in this block.
     */
    private final List<Transaction> transactions;

    /**
     * The timestamp when this block was created.
     */
    private final long timestamp;

    /**
     * The proof of work value for this block.
     */
    private final int proof;

    /**
     * Constructs a new Block with the specified parameters.
     * The timestamp is automatically set to the current time,
     * and the hash is calculated based on all block data.
     *
     * @param blockIndex the index position of this block
     * @param prevHash the hash of the previous block
     * @param blockTransactions the list of transactions to include
     * @param blockProof the proof of work value
     */
    public Block(final int blockIndex,
                 final String prevHash,
                 final List<Transaction> blockTransactions,
                 final int blockProof) {
        this.index = blockIndex;
        this.previousHash = prevHash;
        this.transactions = blockTransactions;
        this.timestamp = System.currentTimeMillis();
        this.proof = blockProof;
        this.hash = calculateHash();
    }

    /**
     * Calculates and returns the hash for this block.
     * The hash is computed using the block's index, previous hash,
     * timestamp, transaction data, and proof of work.
     *
     * @return the calculated hash as a string
     * @throws RuntimeException if hash calculation fails
     */
    public String calculateHash() {
        try {
            StringBuilder transactionData = new StringBuilder();
            for (Transaction transaction : transactions) {
                transactionData.append(
                        new ObjectMapper().writeValueAsString(transaction)
                );
            }
            String input = index + previousHash + timestamp
                    + transactionData + proof;
            return HashUtils.hash(input);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating hash", e);
        }
    }

    /**
     * Returns a JSON string representation of this block.
     *
     * @return JSON representation of the block
     * @throws RuntimeException if serialization fails
     */
    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing Block to JSON", e);
        }
    }
}
