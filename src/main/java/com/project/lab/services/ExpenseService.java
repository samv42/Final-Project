package com.project.lab.services;

import com.project.lab.models.CustomUserDetails;
import com.project.lab.models.Account;
import com.project.lab.models.Expense;
import com.project.lab.repo.ExpenseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class ExpenseService implements ExpenseServiceInterface{
    @Autowired
    ExpenseRepo expenseRepo;

    @Override
    @Cacheable(value = "expenses")
    public List<Expense> getAllExpenses() {
        return expenseRepo.findAll();
    }

    @Override
    @Cacheable(value = "expenses")
    public List<Expense> getExpensesByUser(CustomUserDetails user) {return expenseRepo.getAllExpensesByUser(user);}

    @Override
    @Cacheable(value = "expenses")
    public List <Expense> getExpensesByAccount(Account account){return expenseRepo.getAllExpensesByAccount(account);}

    @Override
    @Transactional
    @CachePut(value = "expenses", key = "#expense.id")
    public Expense saveExpense(Expense expense) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        expense.setUser(user);
        return expenseRepo.save(expense);
    }

    @Override
    @Cacheable(value = "expenses", key = "#id", sync = true)
    public Expense getExpense(Long id) {
        return expenseRepo.findById(id)
                .orElse(null);
    }

    @Override
    @Transactional
    @CacheEvict(value = "expenses", allEntries = true)
    public void deleteExpense(Long id) {
        expenseRepo.deleteById(id);
    }

    @Override
    @Transactional
    @CachePut(value = "expenses", key = "#expense.id")
    public List<Expense> saveAllExpenses(List<Expense> expenseList) {
        return expenseRepo.saveAll(expenseList);
    }


}
