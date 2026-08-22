package com.farmers.general.expense.repository;

import com.farmers.general.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByFarmIdAndDeletedFalseOrderByExpenseDateDesc(Long farmId);

    List<Expense> findByFarmIdAndCategoryAndDeletedFalse(Long farmId, com.farmers.general.expense.enums.ExpenseCategory category);

    List<Expense> findByFarmIdAndExpenseDateBetweenAndDeletedFalse(Long farmId, LocalDate from, LocalDate to);

    List<Expense> findByFarmIdAndCategoryAndExpenseDateBetweenAndDeletedFalse(
            Long farmId, com.farmers.general.expense.enums.ExpenseCategory category, LocalDate from, LocalDate to);
}
