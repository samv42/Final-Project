package com.project.lab.services;

import com.project.lab.models.CustomUserDetails;
import com.project.lab.models.Account;
import com.project.lab.models.Debt;
import com.project.lab.repo.DebtRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class DebtService implements DebtServiceInterface{
    @Autowired
    DebtRepo debtRepo;

    @Override
    public List<Debt> getAllDebts() {
        return debtRepo.findAll();
    }

    @Override
    public List<Debt> getDebtsByAccount(Account account) {
        return debtRepo.getAllDebtsByAccount(account);
    }

    @Override
    public List<Debt> getDebtsByUser(CustomUserDetails user) {return debtRepo.getAllDebtsByUser(user);}

    @Override
    @Transactional
    public Debt saveDebt(Debt debt) {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        debt.setUser(user);
        return debtRepo.save(debt);
    }

    @Override
    public Debt getDebt(Long id) {
        return debtRepo.findById(id)
                .orElse(null);
    }

    @Override
    @Transactional
    public void deleteDebt(Long id) {
        debtRepo.deleteById(id);
    }

    @Override
    @Transactional
    public List<Debt> saveAllDebts(List<Debt> debtList) {
        return debtRepo.saveAll(debtList);
    }
}
