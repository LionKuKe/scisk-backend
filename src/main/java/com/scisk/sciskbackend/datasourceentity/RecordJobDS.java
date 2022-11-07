package com.scisk.sciskbackend.datasourceentity;

import com.scisk.sciskbackend.entity.RecordJob;
import com.scisk.sciskbackend.entity.User;
import com.scisk.sciskbackend.util.GlobalParams;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(GlobalParams.RECORD_JOB_COLLECTION_NAME)
public class RecordJobDS {

    @Id
    private Long id;
    private Instant estimatedStartDate;
    private Instant estimatedEndDate;
    private Instant startDate;
    private Instant endDate;
    private String observation;
    private Instant chiefEndDate;
    private String chiefObservation;

    private Long employeeId;
    private UserDS employee;

    private Long recordStepId;
    private RecordStepDS recordStep;

    private Long jobId;
    private JobDS job;

    public Optional<Long> getEmployeeId() {
        return Optional.ofNullable(employeeId);
    }

    public static RecordJob map(RecordJobDS recordJobDS) {
        return RecordJob.builder()
                .id(recordJobDS.getId())
                .estimatedStartDate(recordJobDS.getEstimatedStartDate())
                .estimatedEndDate(recordJobDS.getEstimatedEndDate())
                .startDate(recordJobDS.getStartDate())
                .endDate(recordJobDS.getEndDate())
                .observation(recordJobDS.getObservation())
                .chiefEndDate(recordJobDS.getChiefEndDate())
                .chiefObservation(recordJobDS.getChiefObservation())
                .employee(UserDS.map(recordJobDS.getEmployee()))
                .recordStep(RecordStepDS.map(recordJobDS.getRecordStep()))
                .job(JobDS.map(recordJobDS.getJob()))
                .build();
    }

    public static RecordJobDS map(RecordJob recordJob) {
        return RecordJobDS.builder()
                .id(recordJob.getId())
                .estimatedStartDate(recordJob.getEstimatedStartDate())
                .estimatedEndDate(recordJob.getEstimatedEndDate())
                .startDate(recordJob.getStartDate())
                .endDate(recordJob.getEndDate())
                .observation(recordJob.getObservation())
                .chiefEndDate(recordJob.getChiefEndDate())
                .chiefObservation(recordJob.getChiefObservation())
                .employeeId(recordJob.getEmployee().orElse(User.builder().build()).getId())
                .recordStepId(Objects.isNull(recordJob.getRecordStep()) ? null : recordJob.getRecordStep().getId())
                .jobId(Objects.isNull(recordJob.getJob()) ? null : recordJob.getJob().getId())
                .build();
    }

}
