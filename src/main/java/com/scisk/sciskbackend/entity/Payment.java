package com.scisk.sciskbackend.entity;

import java.time.Instant;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private Long id;
    private Instant paymentDate;
    private Double amount;
    private String observation;
    private Record record;
}
