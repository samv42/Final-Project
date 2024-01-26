package com.project.lab.repo;

import com.project.lab.models.CustomUserDetails;
import com.project.lab.models.Account;
import com.project.lab.models.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtRepo extends JpaRepository<Debt, Long> {
    List<Debt> getAllDebtsByAccount(Account account);
    List<Debt> getAllDebtsByUser(CustomUserDetails user);

}
