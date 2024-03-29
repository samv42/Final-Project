package com.project.lab.services;

import com.project.lab.models.CustomUserDetails;
import com.project.lab.models.Account;
import com.project.lab.models.Income;

import java.util.List;

public interface IncomeServiceInterface {
    List<Income> getAllIncomes();

    Income saveIncome(Income income);

    List<Income> getIncomesByAccount(Account account);

    List<Income> getIncomesByUser(CustomUserDetails user);

    Income getIncome(Long id);

    void deleteIncome(Long id);

    List<Income> saveAllIncomes(List<Income> incomeList);
}
