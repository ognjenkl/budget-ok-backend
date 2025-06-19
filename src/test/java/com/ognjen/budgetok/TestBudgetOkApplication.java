package com.ognjen.budgetok;

import org.springframework.boot.SpringApplication;

public class TestBudgetOkApplication {

	public static void main(String[] args) {
		SpringApplication.from(BudgetOkApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
