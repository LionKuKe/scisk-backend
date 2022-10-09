package com.scisk.sciskbackend.dto;

import com.scisk.sciskbackend.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReturnDto {
    private Long id;
    private Instant paymentDate;
    private Double amount;
    private String observation;
    private Long recordId;

    public static PaymentReturnDto map(Payment payment) {
        return PaymentReturnDto.builder()
                .id(payment.getId())
                .paymentDate(payment.getPaymentDate())
                .amount(payment.getAmount())
                .observation(payment.getObservation())
                .recordId(Objects.isNull(payment.getRecord()) ? null : payment.getRecord().getId())
                .build();
    }
}
