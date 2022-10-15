package com.scisk.sciskbackend.inputdatasource;

import com.scisk.sciskbackend.datasourceentity.PaymentDS;
import com.scisk.sciskbackend.entity.Payment;
import com.scisk.sciskbackend.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class PaymentInputDSImpl implements PaymentInputDS {

    private final PaymentRepository paymentRepository;

    public PaymentInputDSImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<Payment> findByRecordId(Long recordId) {
        return paymentRepository.findByRecordId(recordId)
                .stream().map(PaymentDS::map).collect(Collectors.toList());
    }

    @Override
    public void save(Payment payment) {
        paymentRepository.save(PaymentDS.map(payment));
    }

    @Override
    public Optional<Payment> findById(Long idValue) {
        return paymentRepository.findById(idValue).map(PaymentDS::map);
    }

    @Override
    public void delete(Payment payment) {
        paymentRepository.delete(PaymentDS.map(payment));
    }
}
