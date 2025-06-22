package com.ognjen.budgetok.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvelopeItem {
    private Long id;
    private String description;
    private double amount;
    private LocalDate date;
    private String category;
}
