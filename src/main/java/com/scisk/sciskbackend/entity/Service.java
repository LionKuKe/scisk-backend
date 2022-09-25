package com.scisk.sciskbackend.entity;

import java.time.Instant;
import java.util.List;

import com.scisk.sciskbackend.util.GlobalParams;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(GlobalParams.SERVICE_COLLECTION_NAME)
public class Service {

    @Id
    private Long id;
    private String name;
    private String description;
    private Boolean enabled;
    private Instant createdOn;

    @Transient
    private List<NeededDocument> neededDocuments;

    @Transient
    private List<Step> steps;
}
