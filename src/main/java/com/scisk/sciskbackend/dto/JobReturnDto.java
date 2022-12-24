package com.scisk.sciskbackend.dto;

import com.scisk.sciskbackend.entity.Job;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobReturnDto {
    private Long id;
    private String name;
    private String description;
    private Integer order;

    private Long stepId;
    private String stepName;

    private Long serviceId;
    private String serviceName;

    public static JobReturnDto map(Job job) {
        return JobReturnDto.builder()
                .id(job.getId())
                .name(job.getName())
                .description(job.getDescription())
                .order(job.getOrder())
                .stepId(job.getStep().getId())
                .stepName(job.getStep().getName())
                .serviceId(job.getStep().getService().getId())
                .serviceName(job.getStep().getService().getName())
                .build();
    }
}
