package com.scisk.sciskbackend.dto;

import com.scisk.sciskbackend.entity.Step;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StepReturnDto {
    private Long id;
    private String name;
    private String description;
    private Integer order;
    private Boolean enabled;

    public static StepReturnDto map(Step step) {
        return StepReturnDto.builder()
                .id(step.getId())
                .name(step.getName())
                .description(step.getDescription())
                .order(step.getOrder())
                .enabled(step.getEnabled())
                .build();
    }
}
