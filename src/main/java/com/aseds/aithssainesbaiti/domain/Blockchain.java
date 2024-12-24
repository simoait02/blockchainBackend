package com.aseds.aithssainesbaiti.domain;

import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    private final ArrayList<Block> chain;
    private static final int DIFFICULTY = 4;

    public Blockchain() {
        chain = new ArrayList<>();
        chain.add(createGenesisBlock());
    }

    public ArrayList<Block> getChain() {
        return chain;
    }

    private Block createGenesisBlock() {
        return new Block(0, "0", new ArrayList<>(), 0); // Genesis block with empty transactions
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public Block addBlock(List<Transaction> transactions) {
        Block previousBlock = getLatestBlock();
        int proof = generateProofOfWork(previousBlock.getProof());
        Block newBlock = new Block(chain.size(), previousBlock.getHash(), transactions, proof);
        chain.add(newBlock);
        return newBlock;
    }

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

            if (!isValidProof(previousBlock.getProof(), currentBlock.getProof())) {
                return false;
            }
        }
        return true;
    }

    private int generateProofOfWork(int lastProof) {
        int proof = 0;
        while (!isValidProof(lastProof, proof)) {
            proof++;
        }
        return proof;
    }

    private boolean isValidProof(int lastProof, int proof) {
        String guess = lastProof + "" + proof;
        String guessHash = HashUtils.hash(guess);
        return guessHash.startsWith("0".repeat(DIFFICULTY)); // Checks for required leading zeros
    }
}
