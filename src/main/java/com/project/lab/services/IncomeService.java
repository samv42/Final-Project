package com.project.lab.services;

import com.project.lab.CustomUserDetails;
import com.project.lab.models.Account;
import com.project.lab.models.Income;
import com.project.lab.repo.IncomeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class IncomeService implements IncomeServiceInterface {
    @Autowired
    IncomeRepo incomeRepo;

    @Override
    @Cacheable(value = "incomes")
    public List<Income> getAllIncomes() {
        return incomeRepo.findAll();
    }

    @Override
    @Cacheable(value = "incomes")
    public List <Income> getIncomesByAccount(Account account) {return incomeRepo.getAllIncomesByAccount(account);}

    @Override
    @Cacheable(value = "incomes")
    public List <Income> getIncomesByUser(CustomUserDetails user) {return incomeRepo.getAllIncomesByUser(user);}

    @Override
    @Transactional
    @CachePut(value = "incomes", key = "#income.id")
    public Income saveIncome(Income income) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        income.setUser(user);
        return incomeRepo.save(income);
    }

    @Override
    @Cacheable(value = "incomes", key = "#incomeId", sync = true)
    public Income getIncome(Long id) {
        return incomeRepo.findById(id)
                .orElse(null);
    }

    @Override
    @Transactional
    @CacheEvict(value = "incomes", allEntries = true)
    public void deleteIncome(Long id) {
        incomeRepo.deleteById(id);
    }

    @Override
    @Transactional
    @CachePut(value = "incomes", key = "#income.id")
    public List<Income> saveAllIncomes(List<Income> incomeList) {
        return incomeRepo.saveAll(incomeList);
    }

}
