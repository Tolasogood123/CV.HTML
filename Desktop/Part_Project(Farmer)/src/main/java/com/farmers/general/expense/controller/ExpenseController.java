package com.farmers.general.expense.controller;

import com.farmers.general.expense.dto.ExpenseRequest;
import com.farmers.general.expense.dto.ExpenseResponse;
import com.farmers.general.expense.enums.ExpenseCategory;
import com.farmers.general.expense.service.ExpenseService;
import com.farmers.general.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "Expense management endpoints")
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping("/farms/{farmId}/expenses")
    @Operation(summary = "Get expenses with optional filters")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpenses(
            @PathVariable Long farmId,
            @RequestParam(required = false) ExpenseCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.ok(expenseService.getExpenses(farmId, category, from, to)));
    }

    @GetMapping("/expenses/{id}")
    @Operation(summary = "Get expense by ID")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpense(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(expenseService.getExpenseById(id)));
    }

    @PostMapping("/farms/{farmId}/expenses")
    @Operation(summary = "Create an expense")
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @PathVariable Long farmId,
            @Valid @RequestBody ExpenseRequest request) {
        // TODO: replace hardcoded userId with authenticated user
        ExpenseResponse response = expenseService.createExpense(farmId, 1L, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Expense created", response));
    }

    @PutMapping("/expenses/{id}")
    @Operation(summary = "Update an expense")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Expense updated", expenseService.updateExpense(id, request)));
    }

    @DeleteMapping("/expenses/{id}")
    @Operation(summary = "Soft delete an expense")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(ApiResponse.ok("Expense deleted", null));
    }
}
