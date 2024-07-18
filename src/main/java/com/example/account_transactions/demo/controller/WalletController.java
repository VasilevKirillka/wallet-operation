package com.example.account_transactions.demo.controller;

import com.example.account_transactions.demo.dto.WalletOperation;
import com.example.account_transactions.demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class WalletController {

    private final WalletService service;

    @GetMapping("/wallets/{id}")
    public ResponseEntity<?> getBalance(@PathVariable UUID id) {
        return service.getBalance(id);
    }

    @PostMapping("/wallet")
    public ResponseEntity<?> changeBalance(@RequestBody WalletOperation operation) {
        return service.changeBalance(operation);
    }
}
