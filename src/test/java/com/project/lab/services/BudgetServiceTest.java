package com.project.lab.services;

import com.project.lab.models.*;
import com.project.lab.repo.AccountRepo;
import com.project.lab.repo.IncomeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = BudgetService.class)

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
    AccountRepo accountRepo;

    @MockBean
    UserDetailsService userDetailsService;

    @Captor
    ArgumentCaptor<Account> argumentCaptor;

    CustomUserDetails user = CustomUserDetails.builder()
            .username("user")
            .build();

    private Account account = Account.builder()
            .name("main")
            .type("Savings")
            .balance(200)
            .interest(0)
            .goalReached(true)
            .user(user)
            .targetBalance(400).build();
    private Income income = Income.builder()
            .name("income")
            .amount(200)
            .recurring(true)
            .date("2022-01-01")
            .account(account)
            .paymentDate("2022-01-01")
            .paymentReceived(false)
            .build();
    private Expense expense = Expense.builder()
            .name("expense")
            .amount(200)
            .recurring(true)
            .date("2022-12-01")
            .paymentDate("2022-01-01")
            .paymentReceived(false)
            .account(account)
            .build();
    private Debt debt = Debt.builder()
            .creditor("Chase")
            .amount(200)
            .interest(.02)
            .date("2022-12-01")
            .paymentDate("2022-01-01")
            .paymentReceived(false)
            .account(account)
            .build();

    @BeforeEach
    void setUser() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    void addRecurringIncome() {
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        when(userDetailsService.loadUserByUsername(any())).thenReturn(user);
        when(accountService.getAccount(any())).thenReturn(account);
        budgetService.addRecurringIncome(income);
        verify(accountService).saveAccount(argumentCaptor.capture());
        verify(accountService, times(1)).saveAccount(account);
        assertEquals(400, argumentCaptor.getValue().getBalance());

    }

    @Test
    void addIncome() {
        when(accountService.getAccount(any())).thenReturn(account);
        budgetService.addIncome(income);
        assertEquals(400d, accountService.getAccount(account.getId()).getBalance());
    }

    @Test
    void checkIncomeListPayments() {
        Account account = Account.builder()
                .name("main")
                .type("Savings")
                .balance(0)
                .interest(0)
                .goalReached(true)
                .user(user)
                .targetBalance(400).build();
        Income income = Income.builder()
                .name("income")
                .amount(200)
                .recurring(true)
                .date("2022-01-01")
                .paymentReceived(false)
                .account(account)
                .build();
        when(incomeService.getAllIncomes()).thenReturn(Arrays.asList(income));
        when(accountService.getAccount(any())).thenReturn(account);
        budgetService.checkIncomeListPayments();

    }

    @Test
    void checkExpenseListPayments() {
        when(expenseService.getAllExpenses()).thenReturn(Arrays.asList(expense));
        when(accountService.getAccount(any())).thenReturn(account);
        budgetService.checkExpenseListPayments();
    }

    @Test
    void subtractRecurringExpense() {
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(user);
        when(userDetailsService.loadUserByUsername(any())).thenReturn(user);
        when(accountService.getAccount(any())).thenReturn(account);
        budgetService.subtractRecurringExpense(expense);
        verify(accountService).saveAccount(argumentCaptor.capture());
        verify(accountService, times(1)).saveAccount(account);
        assertEquals(0, argumentCaptor.getValue().getBalance());
    }

    @Test
    void subtractExpense() {
        when(accountService.getAccount(any())).thenReturn(account);
        budgetService.subtractExpense(expense);
        assertEquals(0d, accountService.getAccount(account.getId()).getBalance());
    }
    @Test
    void subtractDebtPayment() {
        budgetService.subtractDebtPayment(debt);}

    @Test
    void checkDebtListPayments() {
        when(debtService.getAllDebts()).thenReturn(Arrays.asList(debt));
        when(accountService.getAccount(any())).thenReturn(account);
        budgetService.checkDebtListPayments();
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
    void expensePaymentReceived() {
        budgetService.expensePaymentReceived(expense);
    }

    @Test
    void isNextIncomeMonth() {
        income.setPaymentReceived(true);
        budgetService.isNextIncomeMonth(income);
        assertEquals(false, income.isPaymentReceived());
    }

    @Test
    void isNextExpenseMonth() {
        expense.setPaymentReceived(true);
        budgetService.isNextExpenseMonth(expense);
        assertEquals(false, expense.isPaymentReceived());
    }

    @Test
    void isNextDebtMonth() {
        debt.setPaymentReceived(true);
        budgetService.isNextDebtMonth(debt);
        assertEquals(false, debt.isPaymentReceived());
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
    void transferMoney() {
        Account account2 = Account.builder()
                .name("main2")
                .type("Savings")
                .balance(200)
                .interest(0)
                .goalReached(true)
                .user(user)
                .targetBalance(400).build();
        InternalTransfer internalTransfer = InternalTransfer.builder()
                .money(200)
                .targetAccount(account.getId())
                .transferringAccount(account2.getId())
                .build();
        when(accountService.getAccount(any())).thenReturn(account2).thenReturn(account2);
        budgetService.transferMoney(internalTransfer);
        Double actual = account.getBalance();
        assertEquals(200d, actual);
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
        Account account = Account.builder()
                .name("main")
                .balance(500)
                .interest(0)
                .goalReached(false)
                .targetBalance(400).build();
        when(accountService.getAccountsByUser(any())).thenReturn(Arrays.asList(account));
        List<Account> actual = budgetService.checkGoals();
        assertEquals(Arrays.asList(account), actual);
    }


}