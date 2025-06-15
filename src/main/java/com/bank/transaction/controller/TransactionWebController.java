package com.bank.transaction.controller;

import com.bank.transaction.dto.TransactionDto;
import com.bank.transaction.enums.TransactionType;
import com.bank.transaction.model.Transaction;
import com.bank.transaction.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionWebController {

    private final TransactionService transactionService;

    public TransactionWebController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/list")
    public String listTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) TransactionType type,
            Model model) {

        List<Transaction> list = transactionService.listTransactions(page, size, type);
        model.addAttribute("transactions", list);
        model.addAttribute("currentPage", page);
        model.addAttribute("transactionTypes", TransactionType.values());
        model.addAttribute("type",type);
        return "transaction-list";
    }
    
    @GetMapping("/new")
    public String showNewTransactionForm(Model model) {
        TransactionDto transaction = new TransactionDto();
        transaction.setId(transactionService.createTransactionId());
        model.addAttribute("transaction", transaction);
        model.addAttribute("transactionTypes", TransactionType.values());
        model.addAttribute("actionType","create");
        return "transaction-form";
    }

    @PostMapping("/create")
    public String createTransaction(@ModelAttribute TransactionDto transaction, RedirectAttributes redirectAttributes) {
        transactionService.createTransaction(transaction);
        redirectAttributes.addFlashAttribute("message", "Transaction saved successfully!");
        return "redirect:/transactions/list";
    }

    @PostMapping("/edit")
    public String editTransaction(@ModelAttribute TransactionDto transaction, RedirectAttributes redirectAttributes) {
        transactionService.updateTransaction(transaction.getId(),transaction);
        redirectAttributes.addFlashAttribute("message", "Transaction saved successfully!");
        return "redirect:/transactions/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditTransactionForm(@PathVariable Long id, Model model) {
        Transaction transaction = transactionService.getTransaction(id);
        model.addAttribute("transaction", transaction);
        model.addAttribute("transactionTypes", TransactionType.values());
        model.addAttribute("actionType","edit");
        return "transaction-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        transactionService.deleteTransaction(id);
        redirectAttributes.addFlashAttribute("message", "Transaction deleted successfully!");
        return "redirect:/transactions/list";
    }
}