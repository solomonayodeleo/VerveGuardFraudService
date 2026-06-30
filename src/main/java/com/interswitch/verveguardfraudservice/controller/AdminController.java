package com.interswitch.verveguardfraudservice.controller;

import com.interswitch.verveguardfraudservice.entity.TransactionLog;
import com.interswitch.verveguardfraudservice.repository.TransactionLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final TransactionLogRepository repository;

    public AdminController(TransactionLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionLog>> getAllTransactions() {
        return ResponseEntity.ok(repository.findAll());
    }
}
