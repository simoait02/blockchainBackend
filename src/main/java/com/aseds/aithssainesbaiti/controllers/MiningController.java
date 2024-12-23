package com.aseds.aithssainesbaiti.controllers;

import com.aseds.aithssainesbaiti.domain.Block;
import com.aseds.aithssainesbaiti.services.MiningService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mining")
public class MiningController {
    private final MiningService miningService;

    public MiningController(MiningService miningService) {
        this.miningService = miningService;
    }

    @PostMapping
    public Block mineBlock() {
        return miningService.mineBlock();
    }
}