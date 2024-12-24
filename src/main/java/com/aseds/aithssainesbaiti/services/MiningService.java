package com.aseds.aithssainesbaiti.services;

import com.aseds.aithssainesbaiti.domain.*;
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
            if(UserService.users.get(transaction.getSenderId()).getSold()>transaction.getAmount() && UserService.users.get(transaction.getRecipientId())!=null){
                Block newBlock = new Block(
                        blockchain.getChain().size(),
                        lastBlock.getHash(),
                        transaction,
                        proof
                );
                blockchain.addBlock(newBlock.getData());
                UserService.updateSolde(transaction.getSenderId(), UserService.getSold(transaction.getSenderId())-transaction.getAmount());
                UserService.updateSolde(transaction.getRecipientId(), UserService.getSold(transaction.getRecipientId())+transaction.getAmount());
                TransactionService.removeTransaction(transaction);
            }else {
                System.out.println(transaction.getSenderId() + " de " + transaction.getAmount()+" a "+ transaction.getRecipientId() +" removed");
                TransactionService.removeTransaction(transaction);
            }
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
