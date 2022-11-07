package com.scisk.sciskbackend.datasourceentity;

import com.scisk.sciskbackend.entity.Job;
import com.scisk.sciskbackend.util.GlobalParams;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(GlobalParams.JOB_COLLECTION_NAME)
public class JobDS {

    @Id
    private Long id;
    private String name;
    private String description;
    private Integer order;

    private Long stepId;
    private StepDS step;

    public static Job map(JobDS jobDS) {
        return Job.builder()
                .id(jobDS.getId())
                .name(jobDS.getName())
                .description(jobDS.getDescription())
                .order(jobDS.getOrder())
                .step(StepDS.map(jobDS.getStep()))
                .build();
    }

    public static JobDS map(Job job) {
        return JobDS.builder()
                .id(job.getId())
                .name(job.getName())
                .description(job.getDescription())
                .order(job.getOrder())
                .stepId(Objects.isNull(job.getStep()) ? null : job.getId())
                .build();
    }
}
