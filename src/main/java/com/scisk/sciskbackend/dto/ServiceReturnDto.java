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
public class ServiceReturnDto {
    private Long id;
    private String name;
    private String description;
    private Instant createdOn;

    private List<NeededDocumentReturnDto> neededDocuments;

    private List<StepReturnDto> steps;

    public static ServiceReturnDto map(Service service) {
        return ServiceReturnDto.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .neededDocuments(
                        (Objects.isNull(service.getNeededDocuments()) || service.getNeededDocuments().isEmpty()) ?
                                null :
                                service.getNeededDocuments().stream().map(NeededDocumentReturnDto::map).collect(Collectors.toList())
                )
                .steps(
                        (Objects.isNull(service.getSteps()) || service.getSteps().isEmpty()) ?
                                null :
                                service.getSteps().stream().map(StepReturnDto::map).collect(Collectors.toList())
                )
                .createdOn(service.getCreatedOn())
                .build();
    }
}
