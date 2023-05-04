package com.project.lab.services;

import com.project.lab.models.Account;
import com.project.lab.models.Income;
import com.project.lab.models.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentServiceInterface {
    List<Payment> getAllPayments();

    Payment savePayment(Payment payment);

    List<Payment> getPaymentsByAccount(Account account);

    List<Payment> getPaymentsByAccountAndTypeName(Account account, String name);

    Page<Payment> getPaymentsByAccountPageable(Account account, Pageable pageable);

    Payment getPayment(Long id);

    void deletePayment(Long id);
}
