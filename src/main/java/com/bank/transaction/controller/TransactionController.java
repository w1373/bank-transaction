package com.bank.transaction.controller;

import com.bank.transaction.dto.TransactionDto;
import com.bank.transaction.enums.TransactionType;
import com.bank.transaction.model.Response;
import com.bank.transaction.model.Transaction;
import com.bank.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/create")
    public Response<Transaction> createTransaction(@RequestBody @Valid TransactionDto dto) {
        return Response.success(transactionService.createTransaction(dto));
    }

    @GetMapping("/query/{id}")
    public Response<Transaction> getTransaction(@PathVariable Long id) {
        return Response.success(transactionService.getTransaction(id));
    }

    @PutMapping("/update/{id}")
    public Response<Transaction> updateTransaction(@PathVariable Long id, @RequestBody TransactionDto dto) {
        return Response.success(transactionService.updateTransaction(id,dto));
    }

    @DeleteMapping("/{id}")
    public Response<Boolean> deleteTransaction(@PathVariable Long id) {
        return Response.success(transactionService.deleteTransaction(id));
    }

    @GetMapping("/list")
    public Response<List<Transaction>> listTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) TransactionType type) {
        return Response.success(transactionService.listTransactions(page, size, type));
    }
}