package com.scisk.sciskbackend.dto;

import com.scisk.sciskbackend.entity.Service;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordReturnDto {
    private Long id;
    private String name;
    private String description;
    private Instant createdOn;

    private List<NeededDocumentReturnDto> neededDocuments;

    private List<StepReturnDto> steps;

    public static RecordReturnDto map(Service service) {
        return RecordReturnDto.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .neededDocuments(
                        (Objects.isNull(service.getNeededDocuments()) || service.getNeededDocuments().size() == 0) ?
                                null :
                                service.getNeededDocuments().stream().map(NeededDocumentReturnDto::map).collect(Collectors.toList())
                )
                .steps(
                        (Objects.isNull(service.getSteps()) || service.getSteps().size() == 0) ?
                                null :
                                service.getSteps().stream().map(StepReturnDto::map).collect(Collectors.toList())
                )
                .createdOn(service.getCreatedOn())
                .build();
    }
}
