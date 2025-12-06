package com.ognjen.budgetok.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("envelopes")
public class Envelope {

    @Id
    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
    @Min(value = 0, message = "Budget must not be negative")
    private int budget;

    @MappedCollection(idColumn = "envelope_id")
    @Builder.Default
    private List<Expense> expenses = new ArrayList<>();

    @JsonProperty("balance")
    public int getBalance() {
        if (expenses == null || expenses.isEmpty()) {
            return budget;
        }
        int balance = budget;
        for (Expense expense : expenses) {
            if ("WITHDRAW".equals(expense.getTransactionType())) {
                balance -= expense.getAmount();
            } else if ("DEPOSIT".equals(expense.getTransactionType())) {
                balance += expense.getAmount();
            }
        }
        return balance;
    }

    public boolean hasExpenses() {
        return expenses != null && !expenses.isEmpty();
    }

    public double getExpenseAmount(String memo) {
        if (expenses == null) {
            throw new IllegalArgumentException("Expense not found");
        }
        return expenses.stream()
                .filter(expense -> expense.getMemo().equals(memo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Expense not found"))
                .getAmount();
    }

    public boolean add(Expense expense) {
        if (expenses == null) {
            expenses = new ArrayList<>();
        }
        return expenses.add(expense);
    }
}
