package com.Hinga.farmMis.repository;

import com.Hinga.farmMis.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrderId(Long orderId);
    Payment findByStripePaymentId(String stripePaymentId);
} 