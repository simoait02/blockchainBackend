package com.aseds.aithssainesbaiti.services;

import com.aseds.aithssainesbaiti.domain.Block;
import com.aseds.aithssainesbaiti.domain.Blockchain;
import com.aseds.aithssainesbaiti.domain.HashUtils;
import com.aseds.aithssainesbaiti.domain.Transaction;
import org.springframework.stereotype.Service;

@Service
public class MiningService {
    private final BlockchainService blockchainService;

    public MiningService(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }
    public Block mineBlock() {

        Blockchain blockchain = blockchainService.getBlockchain();
        for (Transaction transaction:TransactionService.getPendingTransactions()){
            Block lastBlock = blockchain.getLatestBlock();
            int proof = generateProofOfWork(lastBlock.getProof());
            Block newBlock = new Block(
                    blockchain.getChain().size(),
                    lastBlock.getHash(),
                    transaction,
                    proof
            );
            blockchain.addBlock(newBlock.getData());
        }
        return blockchain.getLatestBlock();
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
        return guessHash.startsWith("0000");
    }

}
