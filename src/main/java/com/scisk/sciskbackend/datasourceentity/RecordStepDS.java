package com.scisk.sciskbackend.datasourceentity;

import com.scisk.sciskbackend.entity.Record;
import com.scisk.sciskbackend.entity.RecordJob;
import com.scisk.sciskbackend.entity.RecordStep;
import com.scisk.sciskbackend.entity.Step;
import com.scisk.sciskbackend.util.GlobalParams;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(GlobalParams.RECORD_STEP_COLLECTION_NAME)
public class RecordStepDS {

    @Id
    private Long id;
    private String name;
    private String observation;
    private Instant endDate;

    private Long recordId;
    private RecordDS record;

    private Long stepId;
    private StepDS step;

    private List<RecordJobDS> recordJobs;

    public static RecordStep map(RecordStepDS recordStepDS) {
        return RecordStep.builder()
                .id(recordStepDS.getId())
                .name(recordStepDS.getName())
                .observation(recordStepDS.getObservation())
                .endDate(recordStepDS.getEndDate())
                .record(RecordDS.map(recordStepDS.getRecord()))
                .step(StepDS.map(recordStepDS.getStep()))
                .recordJobs(recordStepDS.getRecordJobs().stream().map(RecordJobDS::map).collect(Collectors.toList()))
                .build();
    }

    public static RecordStepDS map(RecordStep recordStep) {
        return RecordStepDS.builder()
                .id(recordStep.getId())
                .name(recordStep.getName())
                .observation(recordStep.getObservation())
                .endDate(recordStep.getEndDate())
                .recordId(Objects.isNull(recordStep.getRecord()) ? null : recordStep.getRecord().getId())
                .stepId(Objects.isNull(recordStep.getStep()) ? null : recordStep.getStep().getId())
                .build();
    }

}
