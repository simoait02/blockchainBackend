package com.aseds.aithssainesbaiti.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Blockchain {
    /**
     * The blockchain itself.
     */
    private final ArrayList<Block> chain;
    /**
     * The difficulty of the proof of work algorithm.
     */
    private static final int DIFFICULTY = 4;

    /**
     * Constructs a new blockchain with a single genesis block.
     */
    public Blockchain() {
        chain = new ArrayList<>();
        chain.add(createGenesisBlock());
    }

    /**
     * Creates a genesis block.
     * @return the genesis block
     */
    private Block createGenesisBlock() {
        return new Block(0, "0", new ArrayList<>(), 0);
    }

    /**
     * Retrieves the latest block in the chain.
     * @return the latest block
     */
    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    /**
     * Adds a new block to the chain.
     * @param transactions the transactions to include in the new block
     * @return the newly created block
     */
    public Block addBlock(final List<Transaction> transactions) {
        Block previousBlock = getLatestBlock();
        int proof = generateProofOfWork(previousBlock.getProof());
        Block newBlock = new Block(chain.size(), previousBlock.getHash(), transactions, proof);
        chain.add(newBlock);
        return newBlock;
    }

    /**
     * Validates the integrity of the entire blockchain.
     * @return true if the blockchain is valid, false otherwise
     */
    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                return false;
            }

            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }

            if (isValidProof(previousBlock.getProof(), currentBlock.getProof())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Generates a proof of work for the given last proof.
     * @param lastProof the last proof generated
     * @return the generated proof
     */
    private int generateProofOfWork(final int lastProof) {
        int proof = 0;
        while (isValidProof(lastProof, proof)) {
            proof++;
        }
        return proof;
    }

    /**
     * Checks if a proof is valid.
     * @param lastProof the last proof generated
     * @param proof the current proof to be checked
     * @return true if the proof is valid, false otherwise
     */
    private boolean isValidProof(final int lastProof, final int proof) {
        String guess = lastProof + "" + proof;
        String guessHash = HashUtils.hash(guess);
        return !guessHash.startsWith("0".repeat(DIFFICULTY));
    }
}
