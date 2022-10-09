package com.scisk.sciskbackend.entity;

import java.time.Instant;
import java.util.List;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordStep {
    private Long id;
    private String name;
    private String observation;
    private Instant endDate;

    private Record record;

    private Step step;

    private List<RecordJob> recordJobs;
}
