package com.project.lab.configs;

import com.project.lab.models.Account;
import com.project.lab.models.CustomUserDetails;
import com.project.lab.models.Role;
import com.project.lab.repo.AccountRepo;
import com.project.lab.services.AccountService;
import com.project.lab.services.BudgetUserDetailsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class Config {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
    @Bean
    public CommandLineRunner loadInitialData(BudgetUserDetailsService budgetUserDetailsService, AccountService accountService,
                                             PasswordEncoder passwordEncoder, AccountRepo accountRepo) {

        return (args) -> {
            if (budgetUserDetailsService.getAllUsers().isEmpty()) {
                Role roleAdmin = new Role(Role.Roles.ROLE_ADMIN);
                budgetUserDetailsService.createNewUser(
                        new CustomUserDetails("admin", "strongpass23!",
                                Collections.singletonList(roleAdmin))
                );
                Role roleUser = new Role(Role.Roles.ROLE_USER);
                budgetUserDetailsService.createNewUser(
                        new CustomUserDetails("user", "password",
                                Collections.singletonList(roleUser))
                );
                budgetUserDetailsService.createNewUser(
                        new CustomUserDetails("user2", "password2",
                                Collections.singletonList(roleUser))
                );
            }
            if(accountService.getAllAccounts().isEmpty()){
                Account account1 = new Account("Main", "Checking", 5000,
                        10000, 0.03, budgetUserDetailsService.getUser("user"));
                accountRepo.save(account1);
            }
        };
    }
}
