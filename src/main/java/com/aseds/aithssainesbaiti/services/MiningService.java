package com.aseds.aithssainesbaiti.services;

import com.aseds.aithssainesbaiti.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MiningService {
    private final BlockchainService blockchainService;

    public MiningService(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }
    public Block mineBlock() {
        Blockchain blockchain = blockchainService.getBlockchain();
        List<Transaction> validTransactions = new ArrayList<>();

        for (Transaction transaction : TransactionService.getPendingTransactions()) {
            if (UserService.users.get(transaction.getSenderId()).getSold() > transaction.getAmount() &&
                    UserService.users.get(transaction.getRecipientId()) != null) {
                validTransactions.add(transaction);

                // Update balances
                UserService.updateSolde(transaction.getSenderId(),
                        UserService.getSold(transaction.getSenderId()) - transaction.getAmount());
                UserService.updateSolde(transaction.getRecipientId(),
                        UserService.getSold(transaction.getRecipientId()) + transaction.getAmount());

                // Update transaction histories
                UserService.history.computeIfAbsent(transaction.getSenderId(), k -> new ArrayList<>()).add(transaction);
                UserService.history.computeIfAbsent(transaction.getRecipientId(), k -> new ArrayList<>()).add(transaction);
            } else {
                System.out.println(transaction.getSenderId() + " de " + transaction.getAmount() +
                        " a " + transaction.getRecipientId() + " removed");
            }
            TransactionService.removeTransaction(transaction); // Remove from pending transactions
        }

        if (!validTransactions.isEmpty()) {
            Block lastBlock = blockchain.getLatestBlock();
            int proof = generateProofOfWork(lastBlock.getProof());
            Block newBlock = new Block(
                    blockchain.getChain().size(),
                    lastBlock.getHash(),
                    validTransactions,
                    proof
            );
            blockchain.addBlock(newBlock.getTransactions());
            return newBlock;
        }

        System.out.println("No valid transactions to mine.");
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
