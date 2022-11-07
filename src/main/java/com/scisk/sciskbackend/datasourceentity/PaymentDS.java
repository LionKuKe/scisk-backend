package com.scisk.sciskbackend.datasourceentity;

import com.scisk.sciskbackend.entity.Payment;
import com.scisk.sciskbackend.util.GlobalParams;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Objects;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(GlobalParams.PAYMENT_COLLECTION_NAME)
public class PaymentDS {

    @Id
    private Long id;
    private Instant paymentDate;
    private Double amount;
    private String observation;

    private Long recordId;
    private RecordDS record;

    public static Payment map(PaymentDS paymentDS) {
        return Payment.builder()
                .id(paymentDS.getId())
                .paymentDate(paymentDS.getPaymentDate())
                .amount(paymentDS.getAmount())
                .observation(paymentDS.getObservation())
                .record(Objects.isNull(paymentDS.getRecord()) ? null : RecordDS.map(paymentDS.getRecord()))
                .build();
    }

    public static PaymentDS map(Payment payment) {
        return PaymentDS.builder()
                .id(payment.getId())
                .paymentDate(payment.getPaymentDate())
                .amount(payment.getAmount())
                .observation(payment.getObservation())
                .recordId(Objects.isNull(payment.getRecord()) ? null : payment.getRecord().getId())
                .build();
    }
}
