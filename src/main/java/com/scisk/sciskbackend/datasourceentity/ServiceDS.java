package com.scisk.sciskbackend.datasourceentity;

import com.scisk.sciskbackend.entity.Service;
import com.scisk.sciskbackend.util.GlobalParams;
import lombok.*;
import org.springframework.data.annotation.Id;
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
@Document(GlobalParams.SERVICE_COLLECTION_NAME)
public class ServiceDS {

    @Id
    private Long id;
    private String name;
    private String description;
    private Boolean enabled;
    private Instant createdOn;

    private List<NeededDocumentDS> neededDocuments;

    private List<StepDS> steps;

    public static Service map(ServiceDS serviceDS) {
        return Service.builder()
                .id(serviceDS.getId())
                .name(serviceDS.getName())
                .description(serviceDS.getDescription())
                .enabled(serviceDS.getEnabled())
                .createdOn(serviceDS.getCreatedOn())
                .neededDocuments(Objects.isNull(serviceDS.getNeededDocuments()) ? null : serviceDS.getNeededDocuments().stream().map(NeededDocumentDS::map).collect(Collectors.toList()))
                .steps(Objects.isNull(serviceDS.getSteps()) ? null : serviceDS.getSteps().stream().map(StepDS::map).collect(Collectors.toList()))
                .build();
    }

    public static ServiceDS map(Service service) {
        return ServiceDS.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .enabled(service.getEnabled())
                .createdOn(service.getCreatedOn())
                .build();
    }
}
