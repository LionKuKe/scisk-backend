package com.scisk.sciskbackend.entity;

import java.time.Instant;
import java.util.Optional;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordJob {
    private Long id;
    private Instant estimatedStartDate;
    private Instant estimatedEndDate;
    private Instant startDate;
    private Instant endDate;
    private String observation;
    private Instant chiefEndDate;
    private String chiefObservation;

    private User employee;

    private RecordStep recordStep;

    private Job job;

    public Optional<User> getEmployee() {
        return Optional.ofNullable(employee);
    }
}
