package com.scisk.sciskbackend.datasourceentity;

import com.scisk.sciskbackend.entity.Job;
import com.scisk.sciskbackend.entity.Service;
import com.scisk.sciskbackend.entity.Step;
import com.scisk.sciskbackend.util.GlobalParams;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(GlobalParams.STEP_COLLECTION_NAME)
public class StepDS {

    @Id
    private Long id;
    private String name;
    private String description;
    private Integer order;
    private Boolean enabled;

    private Long serviceId;
    private ServiceDS service;

    private List<JobDS> jobs;

    public static Step map(StepDS stepDS) {
        return Step.builder()
                .id(stepDS.getId())
                .name(stepDS.getName())
                .description(stepDS.getDescription())
                .order(stepDS.getOrder())
                .enabled(stepDS.getEnabled())
                .service(ServiceDS.map(stepDS.getService()))
                .jobs(stepDS.getJobs().stream().map(JobDS::map).collect(Collectors.toList()))
                .build();
    }

    public static StepDS map(Step step) {
        return StepDS.builder()
                .id(step.getId())
                .name(step.getName())
                .description(step.getDescription())
                .order(step.getOrder())
                .enabled(step.getEnabled())
                .serviceId(Objects.isNull(step.getService()) ? null : step.getService().getId())
                .build();
    }

}
