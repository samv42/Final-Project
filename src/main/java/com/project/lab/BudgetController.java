package com.project.lab;

import com.project.lab.models.*;
import com.project.lab.services.*;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Controller
@Log4j2
public class BudgetController {
    @Autowired
    ArticleService articleService;

    @Autowired
    public ExpenseService expenseService;

    @Autowired
    public IncomeService incomeService;

    @Autowired
    public DebtService debtService;

    @Autowired
    public AccountService accountService;

    @Autowired
    public BudgetService budgetService;

    @Autowired
    public PaymentService paymentService;

    @GetMapping({ "/", "/index" })
    public String index(Model model) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Main menu reached");
        budgetService.checkIncomeListPayments();
        budgetService.checkExpenseListPayments();
        budgetService.checkDebtListPayments();
        if(user.checkAuthority(Role.Roles.ROLE_ADMIN.name())){
            model.addAttribute("role", "admin");
        }else {
            model.addAttribute("role", "user");
        }
        return "main-menu";
    }

    @GetMapping("/income")
    public String showIncomePage(Model model) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.checkAuthority(Role.Roles.ROLE_ADMIN.name())){
            model.addAttribute("incomeList", incomeService.getAllIncomes());
            model.addAttribute("role", "admin");
        }else {
            model.addAttribute("incomeList", incomeService.getIncomesByUser(user));
        }
        return "income";
    }
    @GetMapping("/new-income")
    public String showNewIncomePage(Model model, Authentication authentication) {
        if(!accountService.getAllAccounts().isEmpty()) {
            Income income = new Income();
            model.addAttribute("income", income);
            return "new-income";
        }else{
            log.error("Accounts not found. User must have account to send income to.");
            return "Income-Error";
        }
    }
    @GetMapping("/edit-income/{id}")
    public String showEditIncomePage(Model model, @PathVariable(name = "id") long id) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Income income = incomeService.getIncome(id);
        if(income.getUserId() == user.getId()) {
            model.addAttribute("income", incomeService.getIncome(id));
            return "edit-income";
            }else{
            log.error("Couldn't find income for id " + id);
            return "Income-Error";
        }

    }
    @PostMapping(value = "/save-income")
    public String saveIncome(@ModelAttribute("income") Income income) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(income != null && income.getAccount() != null && accountService.getAccount(income.getAccount().getId()) != null) {
            if (user.getId() == accountService.getAccount(income.getAccount().getId()).getUserId()) {

                if (!income.isRecurring()) {
                    budgetService.addIncome(income);
                }
                incomeService.saveIncome(income);
            } else {
                log.error("Account not found.");
            }
        }
        return "redirect:/";
    }

    @PostMapping("/update-income/{id}")
    public String updateIncome(@PathVariable(name = "id") long id, @ModelAttribute("income") Income income, Model model) {
        if (id != income.getId()) {
            log.error("Couldn't find income for id " + id);
            model.addAttribute("message",
                    "Cannot update, customer id " + income.getId()
                            + " doesn't match id to be updated: " + id + ".");
            return "error-page";
        }
        incomeService.saveIncome(income);
        return "redirect:/";
    }
    @RequestMapping("/delete-income/{id}")
    public String deleteIncome(@PathVariable(name = "id") long id) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Income income = incomeService.getIncome(id);
        if(income.getUserId() == user.getId()) {
            incomeService.deleteIncome(id);
            return "redirect:/";
        }else{
            log.error("Couldn't find income for id " + id);
            return "Income-Error";
        }
    }
    //
    //Expense Services
    //
    @GetMapping("/expenses")
    public String showExpensesPage(Model model) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.checkAuthority(Role.Roles.ROLE_ADMIN.name())){
            model.addAttribute("expenseList", expenseService.getAllExpenses());
            model.addAttribute("role", "admin");
        }else {
            model.addAttribute("expenseList", expenseService.getExpensesByUser(user));
            model.addAttribute("role", "user");
        }
        return "expenses";
    }
    @GetMapping("/new-expense")
    public String showNewExpensePage(Model model, Authentication authentication) {
        if(!accountService.getAllAccounts().isEmpty()) {
            Expense expense = new Expense();
            model.addAttribute("expense", expense);
            return "new-expense";
        }else {
            log.error("Accounts not found. User must have account to charge expense to.");
            return "Income-Error";
        }
    }
    @GetMapping("/edit-expense/{id}")
    public String showEditExpensePage(Model model, @PathVariable(name = "id") long id) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Expense expense = expenseService.getExpense(id);
        if(expense.getUserId() == user.getId()) {
            model.addAttribute("expense", expenseService.getExpense(id));
            return "edit-expense";
        }else{
            log.error("Couldn't find expense for id " + id);
            return "Income-Error";
        }
    }
    @PostMapping(value = "/save-expense")
    public String saveExpense(@ModelAttribute("expense") Expense expense) {
        expenseService.saveExpense(expense);
        return "redirect:/";
    }
    @PostMapping("/update-expense/{id}")
    public String updateExpense(@PathVariable(name = "id") long id, @ModelAttribute("expense") Expense expense, Model model) {
        if (id != expense.getId()) {
            log.error("Couldn't find expense for id " + id);
            model.addAttribute("message",
                    "Cannot update, expense id " + expense.getId()
                            + " doesn't match id to be updated: " + id + ".");
            return "error-page";
        }
        expenseService.saveExpense(expense);
        return "redirect:/";
    }
    @RequestMapping("/delete-expense/{id}")
    public String deleteExpense(@PathVariable(name = "id") long id) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Expense expense = expenseService.getExpense(id);
        if(expense.getUserId() == user.getId()) {
            expenseService.deleteExpense(id);
        }else{
            log.error("Couldn't find expense for id " + id);
            return "Income-Error";
        }
        return "redirect:/";
    }

    //
    //Debt Services
    //
    @GetMapping("/debt")
    public String showDebtPage(Model model) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.checkAuthority(Role.Roles.ROLE_ADMIN.name())){
            model.addAttribute("debtList", debtService.getAllDebts());
            model.addAttribute("role", "admin");
        }else {
            model.addAttribute("debtList", debtService.getDebtsByUser(user));
            model.addAttribute("role", "user");
        }
        return "debt";
    }
    @GetMapping("/new-debt")
    public String showNewDebtPage(Model model, Authentication authentication) {
        if(!accountService.getAllAccounts().isEmpty()) {
            Debt debt = new Debt();
            debt.setUser((CustomUserDetails) authentication.getPrincipal());
            model.addAttribute("debt", debt);
            return "new-debt";
        }else{
            log.error("Accounts not found. User must have account to charge debt payments to");
            return "Income-Error";
        }
    }
    @GetMapping("/edit-debt/{id}")
    public String showEditDebtPage(Model model, @PathVariable(name = "id") long id) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Debt debt = debtService.getDebt(id);
        if(debt.getUserId() == user.getId()) {
            model.addAttribute("debt", debtService.getDebt(id));
            return "edit-debt";
        }else {
            log.error("Couldn't find debt for id " + id);
            return "Income-Error";
        }
    }
    @PostMapping(value = "/save-debt")
    public String saveDebt(@ModelAttribute("debt") Debt debt) {
        debtService.saveDebt(debt);
        return "redirect:/";
    }

    @PostMapping("/update-debt/{id}")
    public String updateDebt(@PathVariable(name = "id") long id, @ModelAttribute("debt") Debt debt, Model model) {
        if (id != debt.getId()) {
            log.error("Couldn't find debt for id " + id);
            model.addAttribute("message",
                    "Cannot update, debt id " + debt.getId()
                            + " doesn't match id to be updated: " + id + ".");
            return "error-page";
        }
        debtService.saveDebt(debt);
        return "redirect:/";
    }
    @RequestMapping("/delete-debt/{id}")
    public String deleteDebt(@PathVariable(name = "id") long id) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Debt debt = debtService.getDebt(id);
        if(debt.getUserId() == user.getId()) {
            debtService.deleteDebt(id);
            return "redirect:/";
        }else{
            log.error("Couldn't find debt for id " + id);
            return "Income-Error";
        }
    }
    //
    //Account Services
    //

    @GetMapping("/accounts")
    public String showAccountPage(Model model) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.checkAuthority(Role.Roles.ROLE_ADMIN.name())){
            model.addAttribute("accountList", accountService.getAllAccounts());
            model.addAttribute("role", "admin");
        }else {
            model.addAttribute("accountList", accountService.getAccountsByUser(user));
            model.addAttribute("role", "user");
        }
        return "accounts";
    }
    @GetMapping("/new-account")
    public String showNewAccountPage(Model model, Authentication authentication) {
        Account account = new Account();
        account.setUser((CustomUserDetails) authentication.getPrincipal());
        model.addAttribute("account", account);
        return "new-account";
    }
    @GetMapping("/edit-account/{id}")
    public String showEditAccountPage(Model model, @PathVariable(name = "id") long id) {
        model.addAttribute("account", accountService.getAccount(id));
        return "edit-account";
    }
    @PostMapping(value = "/save-account")
    public String saveAccount(@ModelAttribute("account") Account account) {
        accountService.saveAccount(account);
        return "redirect:/";
    }

    @GetMapping(value = "/incomes-by-account/{id}")
    public String IncomesByAccount(Model model, @PathVariable(name = "id") long id) {
        Account account = accountService.getAccount(id);
        model.addAttribute("incomeList", incomeService.getIncomesByAccount(account));
        return "incomes-by-account";
    }

    @GetMapping(value = "/expenses-by-account/{id}")
    public String ExpensesByAccount(Model model, @PathVariable(name = "id") long id) {
        Account account = accountService.getAccount(id);
        model.addAttribute("expenseList", expenseService.getExpensesByAccount(account));
        return "expenses-by-account";
    }
    @GetMapping(value = "/debts-by-account/{id}")
    public String DebtsByAccount(Model model, @PathVariable(name = "id") long id) {
        Account account = accountService.getAccount(id);
        model.addAttribute("debtList", debtService.getDebtsByAccount(account));
        return "debts-by-account";
    }

    @PostMapping("/update-account/{id}")
    public String updateAccount(@PathVariable(name = "id") long id, @ModelAttribute("account") Account account, Model model) {
        if (id != account.getId()) {
            model.addAttribute("message",
                    "Cannot update, account id " + account.getId()
                            + " doesn't match id to be updated: " + id + ".");
            return "error-page";
        }
        accountService.saveAccount(account);
        return "redirect:/";
    }
    @RequestMapping("/delete-account/{id}")
    public String deleteAccount(@PathVariable(name = "id") long id) {
        accountService.deleteAccount(id);
        return "redirect:/";
    }
    @GetMapping("/transfer-money/{id}")
    public String showTransferMoneyPage(Model model, @PathVariable(name = "id") long id) {
        InternalTransfer internalTransfer = new InternalTransfer();
        internalTransfer.setTransferringAccount(id);
        //model.addAttribute("account", accountService.getAccount(id));
        model.addAttribute("internalTransfer", internalTransfer);
        return "transfer-money";
    }
    @PostMapping("/complete-transfer/{transferringAccount}")
    public String completeTransfer(@PathVariable(name = "transferringAccount") long id,
                                   @ModelAttribute("internalTransfer") InternalTransfer internalTransfer, Model model) {
        if (id != internalTransfer.getTransferringAccount()) {
            model.addAttribute("message",
                    "Cannot transfer, account id " + internalTransfer.getTransferringAccount()
                            + " doesn't match id to transfer from: " + id + ".");
            return "error-page";
        }
        budgetService.transferMoney(internalTransfer);
        return "redirect:/";
    }
    @GetMapping("/Account-Stats/{id}")
    public String showAccountStatisticsPage(Model model, @PathVariable(name = "id") long id) {
        AccountStat accountStat = new AccountStat();
        accountStat.setMonthlyIncome(budgetService.getTotalAccountIncome(id));
        accountStat.setMonthlyExpense(budgetService.getTotalAccountCosts(id));
        if(budgetService.isAccountMakingMoney(id, accountStat.getMonthlyIncome(), accountStat.getMonthlyExpense())){
            accountStat.setTimeToGoal(budgetService.howLongUntilGoal(id, accountStat.getMonthlyIncome(), accountStat.getMonthlyExpense()));
            model.addAttribute("accountStat", accountStat);
            return "Account-Stats-Positive";
        }else{
            accountStat.setTimeToGoal(budgetService.howLongUntilNoMoney(id, accountStat.getMonthlyIncome(), accountStat.getMonthlyExpense()));
            model.addAttribute("accountStat", accountStat);
            return "Account-Stats-Negative";
        }
    }
    /*@RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }*/
    @GetMapping("/statistics")
    public String showStatisticsPage(Model model) {
        return "statistics";
    }

    @GetMapping("/budgetArticles")
    public String budgetArticles(Model model) {
        model.addAttribute("articleList", articleService.getEconomyNews());
        return "Budget-Articles";
    }
    @GetMapping("/accountPayments/{id}")
    public String showPaymentsPage(Model model, @PathVariable(name = "id") long id){
       Account account = accountService.getAccount(id);
       int p = 0;

        PageRequest pageRequest = PageRequest.of(p, 5);
        Page<Payment> page = paymentService.getPaymentsByAccountPageable(account, pageRequest);
       //List<Payment> paymentList = paymentService.getPaymentsByAccount(account);
        model.addAttribute("accountId", id);
       model.addAttribute("page", p);
       model.addAttribute("paymentList", page);
       return "Payments";
    }
    @GetMapping("/nextPaymentPage/{id}/{page}")
    //Path variable page number send back
    public String showNextPaymentPage(@PathVariable(name = "id") long id,
                                      @PathVariable(name = "page") int p,
                                      Model model){
        Account account = accountService.getAccount(id);
        p++;
        PageRequest pageRequest = PageRequest.of(p, 5);
        Page<Payment> page = paymentService.getPaymentsByAccountPageable(account, pageRequest);
        page.getTotalPages();
        try{
            model.addAttribute("accountId", id);
            model.addAttribute("page", p);
            model.addAttribute("paymentList", page);
            return "Payments";
        }catch(Exception e){
            return "Income-Error";
        }
    }
}
