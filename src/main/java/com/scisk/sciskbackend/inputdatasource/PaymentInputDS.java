package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.entity.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentInputDS {
    List<Payment> findByRecordId(Long recordId);

    void save(Payment payment);

    Optional<Payment> findById(Long idValue);

    void delete(Payment payment);
}
