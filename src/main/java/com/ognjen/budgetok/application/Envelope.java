package com.ognjen.budgetok.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Envelope {
    private Long id;
    private String name;
    private double budget;
    @Builder.Default
    private List<EnvelopeItem> items = new ArrayList<>();
}
