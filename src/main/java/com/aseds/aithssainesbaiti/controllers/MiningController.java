package com.aseds.aithssainesbaiti.controllers;

import com.aseds.aithssainesbaiti.domain.Block;
import com.aseds.aithssainesbaiti.services.MiningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for mining operations.
 * This controller is designed as final and not for extension.
 * All methods are self-contained and provide complete mining functionality.
 */
@RestController
@RequestMapping("/api/mining")
public final class MiningController {

    /**
     * Service for mining operations.
     */
    private final MiningService miningService;

    /**
     * Constructor for MiningController.
     *
     * @param service the mining service to be injected
     */
    @Autowired
    public MiningController(final MiningService service) {
        this.miningService = service;
    }

    /**
     * Mines a new block.
     *
     * @return the mined block
     */
    @PostMapping
    public Block mineBlock() {
        return miningService.mineBlock();
    }
}
