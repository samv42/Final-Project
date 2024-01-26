package com.project.lab;

import com.project.lab.models.Account;
import com.project.lab.repo.AccountRepo;
import com.project.lab.services.AccountService;
import com.project.lab.models.Debt;
import com.project.lab.services.DebtService;
import org.hibernate.type.IdentifierBagType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.expression.Lists;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories(bootstrapMode = BootstrapMode.LAZY)
@EnableCaching
public class BudgetingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(BudgetingAppApplication.class, args);
	}

}
