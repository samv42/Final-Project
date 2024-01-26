package com.project.lab.repo;

import com.project.lab.models.CustomUserDetails;
import com.project.lab.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface AccountRepo extends JpaRepository<Account, Long> {
    List<Account> getAllAccountsByUser(CustomUserDetails user);
}