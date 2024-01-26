package com.project.lab.repo;

import com.project.lab.models.CustomUserDetails;
import com.project.lab.models.Account;
import com.project.lab.models.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {
    List<Payment> getAllPaymentsByAccount(Account account);
    List<Payment> getAllPaymentsByUser(CustomUserDetails user);
    List<Payment> getAllPaymentsByAccountAndType(Account account, String type);
    Page<Payment> getAllPaymentsByAccount(Account account, Pageable pageable);
}
