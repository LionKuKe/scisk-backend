package com.scisk.sciskbackend.entity;

import java.util.List;
import java.util.Objects;

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
@Document(GlobalParams.STEP_COLLECTION_NAME)
public class Step {

    @Id
    private Long id;
    private String name;
    private String description;
    private Integer order;
    private Boolean enabled;

    @Transient
    private Service service;
    private Long serviceId;

    private List<Job> jobs;

    public void setService(Service service) {
        this.service = service;
        this.serviceId = Objects.isNull(service) ? null : service.getId();
    }
}
