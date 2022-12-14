package com.scisk.sciskbackend.dto;

import com.scisk.sciskbackend.entity.RecordStep;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordStepReturnDto {
    private Long id;
    private String name;
    private String observation;
    private Instant endDate;

    private Long recordId;

    private Long stepId;

    public static RecordStepReturnDto map(RecordStep recordStep) {
        return RecordStepReturnDto.builder()
                .id(recordStep.getId())
                .name(recordStep.getName())
                .observation(recordStep.getObservation())
                .endDate(recordStep.getEndDate())
                .recordId(Objects.isNull(recordStep.getRecord()) ? null : recordStep.getRecord().getId())
                .stepId(Objects.isNull(recordStep.getStep()) ? null : recordStep.getStep().getId())
                .build();
    }
}
