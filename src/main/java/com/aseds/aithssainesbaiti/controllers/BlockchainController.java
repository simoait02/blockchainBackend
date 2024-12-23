package com.aseds.aithssainesbaiti.controllers;

import com.aseds.aithssainesbaiti.domain.Block;
import com.aseds.aithssainesbaiti.domain.Blockchain;
import com.aseds.aithssainesbaiti.domain.Transaction;
import com.aseds.aithssainesbaiti.services.BlockchainService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blockchain")
public class BlockchainController {
    private final BlockchainService blockchainService;

    public BlockchainController(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }

    @GetMapping
    public Blockchain getBlockchain() {
        return blockchainService.getBlockchain();
    }

    @PostMapping("/add-block")
    public Block addBlock(@RequestBody Transaction data) {
        return blockchainService.addBlock(data);
    }

    @GetMapping("/validate")
    public boolean validateBlockchain() {
        return blockchainService.isBlockchainValid();
    }
}
