package com.scisk.sciskbackend.entity;

import java.time.Instant;
import java.util.List;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Service {
    private Long id;
    private String name;
    private String description;
    private Boolean enabled;
    private Instant createdOn;

    private List<NeededDocument> neededDocuments;

    private List<Step> steps;
}
