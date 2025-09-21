package com.aseds.aithssainesbaiti.controllers;

import com.aseds.aithssainesbaiti.domain.Block;
import com.aseds.aithssainesbaiti.domain.Blockchain;
import com.aseds.aithssainesbaiti.domain.Transaction;
import com.aseds.aithssainesbaiti.services.BlockchainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for blockchain operations.
 * This controller is designed as final and not for extension.
 * All methods are self-contained and provide complete blockchain functionality.
 */
@RestController
@RequestMapping("/api/blockchain")
public final class BlockchainController {

    /**
     * Service for blockchain operations.
     */
    private final BlockchainService service;

    /**
     * Constructor for BlockchainController.
     *
     * @param blockchainService the blockchain service to be injected
     */
    @Autowired
    public BlockchainController(final BlockchainService blockchainService) {
        this.service = blockchainService;
    }

    /**
     * Retrieves the current blockchain.
     *
     * @return the current blockchain instance
     */
    @GetMapping
    public Blockchain getBlockchain() {
        return service.getBlockchain();
    }

    /**
     * Adds a new block to the blockchain with the provided transaction data.
     *
     * @param data the list of transactions to include in the new block
     * @return the newly created block
     */
    @PostMapping("/add-block")
    public Block addBlock(@RequestBody final List<Transaction> data) {
        return service.addBlock(data);
    }

    /**
     * Validates the integrity of the entire blockchain.
     *
     * @return true if the blockchain is valid, false otherwise
     */
    @GetMapping("/validate")
    public boolean validateBlockchain() {
        return service.isBlockchainValid();
    }
}
