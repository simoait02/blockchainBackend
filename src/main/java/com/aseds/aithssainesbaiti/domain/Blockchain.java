package com.aseds.aithssainesbaiti.domain;


import java.util.ArrayList;

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
        return new Block(0, "0", new Transaction("simo","s0",10), 0);
    }

    public Block getLatestBlock() {
        return chain.getLast();
    }

    public Block addBlock(Transaction data) {
        Block previousBlock = getLatestBlock();
        int proof = generateProofOfWork(previousBlock.getProof());
        Block newBlock = new Block(chain.size(), previousBlock.getHash(), data, proof);
        chain.add(newBlock);
        return newBlock;
    }

    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            // Check if the hash is correct
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                return false;
            }

            // Check if the previous hash matches
            if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }

            // Validate the proof of work
            if (!isValidProof(previousBlock.getProof(), currentBlock.getProof())) {
                return false;
            }
        }
        return true;
    }

    public void printChain() {
        for (Block block : chain) {
            System.out.println("Index: " + block.getIndex());
            System.out.println("Previous Hash: " + block.getPreviousHash());
            System.out.println("Hash: " + block.getHash());
            System.out.println("Data: " + block.getData());
            System.out.println("Timestamp: " + block.getTimestamp());
            System.out.println("Proof: " + block.getProof());
            System.out.println("------------------------");
        }
    }

    // Proof of Work: Generates a valid proof
    private int generateProofOfWork(int lastProof) {
        int proof = 0;
        while (!isValidProof(lastProof, proof)) {
            proof++;
        }
        return proof;
    }

    // Validates the proof
    private boolean isValidProof(int lastProof, int proof) {
        String guess = lastProof + "" + proof;
        String guessHash = HashUtils.hash(guess);
        return guessHash.startsWith("0".repeat(DIFFICULTY)); // Checks for required leading zeros
    }
}
