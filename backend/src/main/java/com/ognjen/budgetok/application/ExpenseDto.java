package com.ognjen.budgetok.application;

public record ExpenseDto(double amount, String memo, String transactionType, Long bankExpenseId) {
}
