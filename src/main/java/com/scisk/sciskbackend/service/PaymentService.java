package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.PaymentCreateDto;
import com.scisk.sciskbackend.dto.PaymentReturnDto;
import org.springframework.data.domain.Page;

public interface PaymentService {

    PaymentReturnDto create(PaymentCreateDto paymentCreateDto);

    PaymentReturnDto update(Long idValue, PaymentCreateDto paymentCreateDto);

    Page<PaymentReturnDto> findAllPaymentByFilters(Integer page, Integer size, String observation, Long recordId);

    PaymentReturnDto findById(Long idValue);

    void delete(Long idValue);
}
