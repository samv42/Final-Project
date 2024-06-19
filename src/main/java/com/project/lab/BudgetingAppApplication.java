package com.project.lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class BudgetingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(BudgetingAppApplication.class, args);
	}

}
