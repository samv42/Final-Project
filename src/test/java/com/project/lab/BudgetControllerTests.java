package com.project.lab;

import com.project.lab.models.Account;
import com.project.lab.models.Debt;
import com.project.lab.models.Expense;
import com.project.lab.models.Income;
import com.project.lab.services.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(BudgetController.class)
@WithMockUser(value = "user")
public class BudgetControllerTests {
    @MockBean
    IncomeService incomeService;

    @MockBean
    ExpenseService expenseService;

    @MockBean
    DebtService debtService;

    @MockBean
    AccountService accountService;

    @MockBean
    BudgetService budgetService;

    @MockBean
    BudgetUserDetailsService budgetUserDetailsService;

    @MockBean
    PasswordEncoder passwordEncoder;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testIncomePageNormal() throws Exception {
        CustomUserDetails you = new CustomUserDetails();
        you.setUsername("user");
        Account account = Account.builder()
                .id(1)
                .user(you)
                .build();
        Income income = Income.builder()
                .name("main")
                .date("02-02-2020")
                .amount(4000)
                .recurring(true)
                .account(account)
                .user(you)
                .build();
        when(incomeService.getIncomesByUser(any())).thenReturn(Collections.singletonList(income));
        mockMvc.perform(get("/income").with(user(you)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(model().attribute("incomeList", hasSize(1)));
    }

    @Test
    public void testNewIncomeNormal() throws Exception {
        CustomUserDetails you = new CustomUserDetails();
        you.setUsername("user");
        mockMvc .perform(get("/new-income").with(user(you)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(model().attribute("income", "income"));
    }

    @Test
    public void testEditIncomeByIdNormal() throws Exception {
        CustomUserDetails user1 = CustomUserDetails.builder()
                .id((long)1)
                .username("user")
                .build();
        Account account = Account.builder()
                .id(1)
                .user(user1)
                .build();
        Income income = Income.builder()
                .id(1)
                .name("main")
                .date("2020-02-02")
                .amount(4000)
                .recurring(true)
                .user(user1)
                .account(account)
                .build();
        when(incomeService.getIncome(anyLong())).thenReturn(income);
        mockMvc.perform(get("/edit-income/1").with(user(user1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(model().attribute("income", "income"));
    }

    @Test
    public void testDeleteIncomeByIDNormal() throws Exception {
        CustomUserDetails user1 = new CustomUserDetails();
        user1.setUsername("user");
        Income income = Income.builder().name("main").date("02-02-2020").amount(4000).recurring(true).build();
        income.setUser(user1);
        when(incomeService.getIncome(anyLong())).thenReturn(income);
        mockMvc.perform(get("/delete-income/1").with(user(user1)))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testGetIncomeByAccount() throws Exception {
        CustomUserDetails user1 = new CustomUserDetails();
        user1.setUsername("user");
        Account account = new Account();
        account.setUser(user1);
        Income income = Income.builder().name("main").date("02-02-2020").amount(4000).recurring(true).
                account(account).user(user1).build();
        when(accountService.getAccount((long)1)).thenReturn(account);
        when(incomeService.getIncomesByAccount(account)).thenReturn(Collections.singletonList(income));
        mockMvc.perform(get("/incomes-by-account/1").with(user(user1)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("incomeList", hasSize(1)));
    }
    @Test
    public void testExpensePageNormal() throws Exception {
        CustomUserDetails user1 = new CustomUserDetails();
        user1.setUsername("user");
        Account account = Account.builder()
                .id(1)
                .user(user1)
                .build();
        Expense expense = Expense.builder()
                .name("main")
                .date("02-02-2020")
                .amount(4000)
                .recurring(true)
                .account(account)
                .user(user1)
                .build();
        when(expenseService.getExpensesByUser(any())).thenReturn(Collections.singletonList(expense));
        mockMvc.perform(get("/expenses").with(user(user1)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/html;charset=UTF-8"))
                    .andExpect(model().attribute("expenseList", hasSize(1)));

        }
        @Test
        public void testNewExpenseNormal() throws Exception {
            mockMvc .perform(get("/new-expense"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("text/html;charset=UTF-8"))
                    .andExpect(model().attribute("expense", "expense"));
        }
    @Test
    public void testEditExpenseByIdNormal() throws Exception {
        CustomUserDetails user1 = CustomUserDetails.builder()
                .id((long)1)
                .username("user")
                .build();
        Account account = Account.builder()
                .id(1)
                .user(user1)
                .build();
        Expense expense = Expense.builder()
                .id(1)
                .name("main")
                .date("2020-02-02")
                .amount(4000)
                .recurring(true)
                .user(user1)
                .account(account)
                .build();
        when(expenseService.getExpense(anyLong())).thenReturn(expense);
        mockMvc.perform(get("/edit-expense/1").with(user(user1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(model().attribute("expense", "expense"));
    }

    @Test
    public void testDeleteExpenseByIDNormal() throws Exception {
        CustomUserDetails user1 = new CustomUserDetails();
        user1.setUsername("user");
        user1.setId(1l);
        Expense expense = Expense.builder()
                .name("main")
                .date("02-02-2020")
                .amount(4000)
                .recurring(true).build();
        expense.setUser(user1);
        when(expenseService.getExpense(anyLong())).thenReturn(expense);
        mockMvc.perform(get("/delete-expense/1").with(user(user1)))
                .andExpect(status().is3xxRedirection());
    }
    @Test
    public void testGetExpenseByAccount() throws Exception {
        CustomUserDetails user1 = new CustomUserDetails();
        user1.setUsername("user");
        Account account = new Account();
        account.setUser(user1);
        Expense expense = Expense.builder()
                .name("main")
                .date("02-02-2020")
                .amount(4000)
                .recurring(true)
                .account(account)
                .user(user1)
                .build();
        when(accountService.getAccount((long)1)).thenReturn(account);
        when(expenseService.getExpensesByAccount(account)).thenReturn(Collections.singletonList(expense));
        mockMvc.perform(get("/expenses-by-account/1").with(user(user1)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("expenseList", hasSize(1)));
    }
    @Test
    public void testDebtPageNormal() throws Exception {
        CustomUserDetails user1 = new CustomUserDetails();
        user1.setUsername("user");
        Account account = Account.builder()
                .id(1)
                .user(user1)
                .build();
        Debt debt = Debt.builder()
                .creditor("main")
                .date("02-02-2020")
                .amount(4000)
                .interest(.02)
                .account(account)
                .user(user1)
                .build();
        when(debtService.getDebtsByUser(any())).thenReturn(Collections.singletonList(debt));
        mockMvc.perform(get("/debt").with(user(user1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(model().attribute("debtList", hasSize(1)));

    }
    @Test
    public void testAccountPageNormal() throws Exception {
        CustomUserDetails user1 = new CustomUserDetails();
        user1.setUsername("user");
        Account account = Account.builder()
                .id(1)
                .name("main")
                .balance(300)
                .interest(.02)
                .user(user1)
                .build();
        when(accountService.getAccountsByUser(any())).thenReturn(Collections.singletonList(account));
        mockMvc.perform(get("/accounts").with(user(user1)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(model().attribute("accountList", hasSize(1)));
    }
    }

