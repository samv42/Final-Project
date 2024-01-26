package com.project.lab.repo;

import com.project.lab.models.CustomUserDetails;
import com.project.lab.models.Account;
import com.project.lab.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepo extends JpaRepository<Expense, Long> {
    List<Expense> getAllExpensesByAccount(Account account);
    List<Expense> getAllExpensesByUser(CustomUserDetails user);
}
