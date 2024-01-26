package com.project.lab.services;

import com.project.lab.models.CustomUserDetails;
import com.project.lab.models.Account;
import com.project.lab.models.Payment;
import com.project.lab.repo.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService implements PaymentServiceInterface{
    @Autowired
    PaymentRepo paymentRepo;

    @Override
    public List<Payment> getAllPayments(){return paymentRepo.findAll();}

    @Override
    public Payment savePayment(Payment payment){
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        payment.setUser(user);
        return paymentRepo.save(payment);}

    @Override
    public List<Payment> getPaymentsByAccount(Account account){return paymentRepo.getAllPaymentsByAccount(account);}

    @Override
    public Page<Payment> getPaymentsByAccountPageable(Account account, Pageable pageable){
        return paymentRepo.getAllPaymentsByAccount(account, pageable);}

    @Override
    public List<Payment> getPaymentsByAccountAndTypeName(Account account, String type){
        return paymentRepo.getAllPaymentsByAccountAndType(account, type);}

    @Override
    public Payment getPayment(Long id){return paymentRepo.findById(id).orElse(null);}

    public void deletePayment(Long id){paymentRepo.deleteById(id);}
}
