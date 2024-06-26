package com.project.lab.controllers;

import com.project.lab.models.*;
import com.project.lab.services.*;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Log4j2
public class BudgetController {
    public static final String Next_Payment_Page = "/NextPaymentPage";

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
        List<Account> goalsReached = budgetService.checkGoals();
        if(!goalsReached.isEmpty()) {
            model.addAttribute("goalReached", true);
            model.addAttribute("accountList", Strings.join(goalsReached.stream().map(a -> a.getId()).collect(Collectors.toList()),
                    '|'));
        }
        if(accountService.getAccountsByUser(user).isEmpty()) {
            model.addAttribute("noAccounts", true);
        }
        return "main-menu";
    }

    @GetMapping("/goal-reached")
    public String showGoalReachedPage(@RequestParam("id") String accountsId, Model model ) {
        String[] idList = accountsId.split("\\|");
        List<Account> accounts = new ArrayList<>();
        for(String id: idList){
            accounts.add(accountService.getAccount(Long.parseLong(id)));
        }
        model.addAttribute("accountList", accounts);
        return "goals-reached";
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
            String error = "Accounts not found. User must have account to send income to.";
            log.error(error);
            model.addAttribute("message", error);
            return "error-page";
        }
    }
    @GetMapping("/edit-income/{id}")
    public String showEditIncomePage(Model model, @PathVariable(name = "id") long id) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Income income = incomeService.getIncome(id);
        if(income!=null && income.getUserId() == user.getId()) {
            model.addAttribute("income", incomeService.getIncome(id));
            return "edit-income";
            }else{
            String error = ("Couldn't find income for id " + id);
            log.error(error);
            model.addAttribute("message", error);
            return "error-page";
        }

    }
    @PostMapping(value = "/save-income")
    public String saveIncome(@ModelAttribute("income") Income income, Model model) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(income != null && income.getAccount() != null && accountService.getAccount(income.getAccount().getId()) != null) {
            if (user.getId() == accountService.getAccount(income.getAccount().getId()).getUserId()) {

                if (!income.isRecurring()) {
                    budgetService.addIncome(income);
                }
                incomeService.saveIncome(income);
            } else {
                log.error("Account not found.");
                model.addAttribute("message", "Account not found.");
                return "error-page";
            }
        }else{
            log.error("Account not found.");
            model.addAttribute("message", "Account not found.");
            return "error-page";
        }
        return "redirect:/";
    }

    @PostMapping("/update-income/{incomeId}")
    public String updateIncome(@PathVariable(name = "incomeId") long id, @ModelAttribute("income") Income income, Model model) {
        if (id != income.getId()) {
            log.error("Couldn't find income for id " + id);
            model.addAttribute("message",
                    "Cannot update, income id " + income.getId()
                            + " doesn't match id to be updated: " + id + ".");
            return "error-page";
        }
        incomeService.saveIncome(income);
        return "redirect:/";
    }
    @RequestMapping("/delete-income/{id}")
    public String deleteIncome(@PathVariable(name = "id") long id, Model model) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Income income = incomeService.getIncome(id);
        if(income.getUserId() == user.getId() && income != null) {
            incomeService.deleteIncome(id);
            return "redirect:/";
        }else{
            log.error("Couldn't find income for id " + id);
            model.addAttribute("message", "Couldn't find income for id " + id);
            return "error-page";
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
            model.addAttribute("message", "Accounts not found. User must have account to charge expense to.");
            return "error-page";
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
            model.addAttribute("message", "Couldn't find expense with id " + id);
            return "error-page";
        }
    }
    @PostMapping(value = "/save-expense")
    public String saveExpense(@ModelAttribute("expense") Expense expense, Model model) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(expense != null && expense.getAccount() != null && accountService.getAccount(expense.getAccount().getId()) != null) {
            if (user.getId() == accountService.getAccount(expense.getAccount().getId()).getUserId()) {

                if (!expense.isRecurring()) {
                    budgetService.subtractExpense(expense);
                }
                expenseService.saveExpense(expense);
            } else {
                log.error("Account not found.");
                model.addAttribute("message", "Account not found.");
                return "error-page";
            }
        }else{
            log.error("Account not found.");
            model.addAttribute("message", "Account not found.");
            return "error-page";
        }
        expenseService.saveExpense(expense);
        return "redirect:/";
    }
    @PostMapping("/update-expense/{expenseId}")
    public String updateExpense(@PathVariable(name = "expenseId") long id, @ModelAttribute("expense") Expense expense, Model model) {
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
    public String deleteExpense(@PathVariable(name = "id") long id, Model model) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Expense expense = expenseService.getExpense(id);
        if(expense.getUserId() == user.getId()) {
            expenseService.deleteExpense(id);
        }else{
            log.error("Couldn't find expense for id " + id);
            model.addAttribute("message", "Couldn't find expense with id " + id);
            return "error-page";
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
            model.addAttribute("message", "Accounts not found. User must have account to charge debt payments to");
            return "error-page";
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
            log.error("Could not find debt for id " + id);
            model.addAttribute("message", "Could not find debt for id " + id);
            return "error-page";
        }
    }
    @PostMapping(value = "/save-debt")
    public String saveDebt(@ModelAttribute("debt") Debt debt) {
        debtService.saveDebt(debt);
        return "redirect:/";
    }

    @PostMapping("/update-debt/{debtId}")
    public String updateDebt(@PathVariable(name = "debtId") long id, @ModelAttribute("debt") Debt debt, Model model) {
        if (id != debt.getId()) {
            debt.setId(id);
        }
        debtService.saveDebt(debt);
        return "redirect:/";
    }
    @RequestMapping("/delete-debt/{id}")
    public String deleteDebt(@PathVariable(name = "id") long id, Model model) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Debt debt = debtService.getDebt(id);
        if(debt.getUserId() == user.getId()) {
            debtService.deleteDebt(id);
            return "redirect:/";
        }else{
            log.error("Couldn't find debt for id " + id);
            model.addAttribute("message", "Couldn't find debt for id " + id);
            return "error-page";
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
        Account account = accountService.getAccount(id);
        if (account == null) {
            log.error("Cannot find account with id " + id);
            String errorMessage = ("Cannot find account with id " + id);
            model.addAttribute("message", errorMessage);
            return "error-page";
        }
        model.addAttribute("account", account);
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
        Account checkAccount = accountService.getAccount(id);
        if (account == null) {
            log.error("Cannot find account with id " + id);
            String errorMessage = ("Cannot find account with id " + id);
            model.addAttribute("message", errorMessage);
            return "error-page";
        }

        //ensure goal reached is reset when account is edited
        account.setGoalReached(false);
        accountService.saveAccount(account);
        return "redirect:/";
    }
    @RequestMapping("/delete-account/{id}")
    public String deleteAccount(@PathVariable(name = "id") long id, Model model) {
        Account account = accountService.getAccount(id);
        if(incomeService.getIncomesByAccount(account).isEmpty()
                && expenseService.getExpensesByAccount(account).isEmpty()
                && debtService.getDebtsByAccount(account).isEmpty()) {
            accountService.deleteAccount(id);
            return "redirect:/";
        }else{
            String errorMessage = ("You must delete incomes, expenses, and debts associated with the account before deleting.");
            model.addAttribute("message", errorMessage);
            return "error-page";
        }
    }
    @GetMapping("/transfer-money/{id}")
    public String showTransferMoneyPage(Model model, @PathVariable(name = "id") long id) {
        InternalTransfer internalTransfer = new InternalTransfer();
        model.addAttribute("internalTransfer", internalTransfer);
        model.addAttribute("accountId", id);
        return "transfer-money";
    }
    @PostMapping("/complete-transfer/{accountId}")
    public String completeTransfer(@PathVariable(name = "accountId") long id,
                                   @ModelAttribute("internalTransfer") InternalTransfer internalTransfer, Model model) {
        internalTransfer.setTransferringAccount(id);
        if (accountService.getAccount(internalTransfer.getTargetAccount()) == null) {
            model.addAttribute("message",
                    "Cannot find account with id " + internalTransfer.getTargetAccount() + ".");
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
    @GetMapping(Next_Payment_Page + "/{id}/{page}")
    //Path variable page number send back
    public String showNextPaymentPage(@PathVariable(name = "id") long id,
                                      @PathVariable(name = "page") int p,
                                      Model model){
        Account account = accountService.getAccount(id);
        p++;
        PageRequest pageRequest = PageRequest.of(p, 5);
        Page<Payment> page = paymentService.getPaymentsByAccountPageable(account, pageRequest);
        if(page.isEmpty()){
            p--;
            PageRequest lastPageRequest = PageRequest.of(p, 5);
            page = paymentService.getPaymentsByAccountPageable(account, lastPageRequest);
        }
        try{
            model.addAttribute("accountId", id);
            model.addAttribute("page", p);
            model.addAttribute("paymentList", page);
            return "Payments";
        }catch(Exception e){
            model.addAttribute("message", "Could not find payments.");
            return "error-page";
        }
    }
}
