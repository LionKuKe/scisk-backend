package com.scisk.sciskbackend.entity;

import com.scisk.sciskbackend.util.GlobalParams;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(GlobalParams.JOB_COLLECTION_NAME)
public class Job {
    private Long id;
    private String name;
    private String description;
    private Integer order;
    private Step step;
}
