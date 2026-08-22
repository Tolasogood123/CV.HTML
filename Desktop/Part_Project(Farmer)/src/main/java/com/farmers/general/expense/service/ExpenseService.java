package com.farmers.general.expense.service;

import com.farmers.general.exception.ResourceNotFoundException;
import com.farmers.general.expense.dto.ExpenseRequest;
import com.farmers.general.expense.dto.ExpenseResponse;
import com.farmers.general.expense.entity.Expense;
import com.farmers.general.expense.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public List<ExpenseResponse> getExpenses(Long farmId, com.farmers.general.expense.enums.ExpenseCategory category,
                                              LocalDate from, LocalDate to) {
        List<Expense> expenses;

        if (category != null && from != null && to != null) {
            expenses = expenseRepository.findByFarmIdAndCategoryAndExpenseDateBetweenAndDeletedFalse(farmId, category, from, to);
        } else if (category != null) {
            expenses = expenseRepository.findByFarmIdAndCategoryAndDeletedFalse(farmId, category);
        } else if (from != null && to != null) {
            expenses = expenseRepository.findByFarmIdAndExpenseDateBetweenAndDeletedFalse(farmId, from, to);
        } else {
            expenses = expenseRepository.findByFarmIdAndDeletedFalseOrderByExpenseDateDesc(farmId);
        }

        return expenses.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ExpenseResponse getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .filter(e -> !e.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
        return toResponse(expense);
    }

    public ExpenseResponse createExpense(Long farmId, Long userId, ExpenseRequest request) {
        Expense expense = Expense.builder()
                .farmId(farmId)
                .category(request.getCategory())
                .amount(request.getAmount())
                .description(request.getDescription())
                .expenseDate(request.getExpenseDate())
                .receiptUrl(request.getReceiptUrl())
                .createdBy(userId)
                .build();

        expense = expenseRepository.save(expense);
        return toResponse(expense);
    }

    public ExpenseResponse updateExpense(Long id, ExpenseRequest request) {
        Expense expense = expenseRepository.findById(id)
                .filter(e -> !e.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));

        expense.setCategory(request.getCategory());
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setReceiptUrl(request.getReceiptUrl());

        expense = expenseRepository.save(expense);
        return toResponse(expense);
    }

    @Transactional
    public void deleteExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
                .filter(e -> !e.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
        expense.setDeleted(true);
        expenseRepository.save(expense);
    }

    private ExpenseResponse toResponse(Expense expense) {
        ExpenseResponse response = new ExpenseResponse();
        response.setId(expense.getId());
        response.setFarmId(expense.getFarmId());
        response.setCategory(expense.getCategory());
        response.setAmount(expense.getAmount());
        response.setDescription(expense.getDescription());
        response.setExpenseDate(expense.getExpenseDate());
        response.setReceiptUrl(expense.getReceiptUrl());
        response.setCreatedBy(expense.getCreatedBy());
        response.setCreatedAt(expense.getCreatedAt());
        response.setUpdatedAt(expense.getUpdatedAt());
        return response;
    }
}
