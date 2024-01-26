package com.project.lab.services;

import com.project.lab.models.Account;
import com.project.lab.models.Income;
import com.project.lab.repo.IncomeRepo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = BudgetService.class)
//@WithMockUser
class BudgetServiceTest {
    @MockBean
    IncomeService incomeService;

    @MockBean
    ExpenseService expenseService;

    @MockBean
    DebtService debtService;

    @MockBean
    AccountService accountService;

    @MockBean
    PaymentService paymentService;

    @Autowired
    BudgetService budgetService;

    @MockBean
    IncomeRepo incomeRepo;

    @Test
    void addRecurringIncome() {
        Account account = Account.builder()
                .name("main")
                .type("Savings")
                .balance(0)
                .interest(0)
                .goalReached(true)
                .targetBalance(400).build();
        Income income = Income.builder()
                .name("income")
                .amount(200)
                .recurring(true)
                .date("2022-08-08")
                .build();
        when(accountService.getAccount(any())).thenReturn(account);
        budgetService.addRecurringIncome(income);

        Mockito.verify(incomeRepo, times(1)).save(income);

    }

    @Test
    void getTotalAccountIncome() {
        when(incomeService.getIncomesByAccount(any())).thenReturn(new ArrayList<>());
        double actual = budgetService.getTotalAccountIncome(0);
        assertEquals(0, actual);
    }
    @Test
    void getTotalAccountCosts() {
        when(expenseService.getExpensesByAccount(any())).thenReturn(new ArrayList<>());
        when(debtService.getDebtsByAccount(any())).thenReturn(new ArrayList<>());
        double actual = budgetService.getTotalAccountCosts(0);
        assertEquals(0, actual);
    }

    @Test
    void howLongUntilGoal() {
        Account account = Account.builder()
                .name("main")
                .balance(0)
                .interest(0)
                .targetBalance(400).build();
        when(accountService.getAccount((long)1)).thenReturn(account);
        int actual = budgetService.howLongUntilGoal((long)1, (double)200, (double)0);
        assertEquals(2, actual);
    }
    @Test
    void howLongUntilNoMoney() {
        Account account = Account.builder()
                .name("main")
                .balance(400)
                .interest(0)
                .targetBalance(400).build();
        when(accountService.getAccount((long)1)).thenReturn(account);
        int actual = budgetService.howLongUntilNoMoney((long)1, (double)0, (double) 200);
        assertEquals(2, actual);
    }
    @Test
    void isAccountMakingMoney() {
        Account account = Account.builder()
                .name("main")
                .balance(400)
                .interest(0)
                .targetBalance(400).build();
        when(accountService.getAccount((long)1)).thenReturn(account);
        boolean actual = budgetService.isAccountMakingMoney((long)1, (double)200, (double) 0);
        assertEquals(true, actual);
    }
    @Test
    void checkGoal() {
        Account account = Account.builder()
                .name("main")
                .balance(400)
                .interest(0)
                .goalReached(false)
                .targetBalance(400).build();
        boolean actual = budgetService.checkGoal(account);
        assertEquals(true, actual);
    }
    @Test
    void checkGoals() {
        //CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account account = Account.builder()
                .name("main")
                .balance(500)
                .interest(0)
                .goalReached(false)
                .targetBalance(400).build();
        when(accountService.getAccountsByUser(any())).thenReturn(Arrays.asList(account));
        when(budgetService.checkGoal(account)).thenReturn(true);
        List<Account> actual = budgetService.checkGoals();
        assertEquals(Arrays.asList(account), actual);
    }


}