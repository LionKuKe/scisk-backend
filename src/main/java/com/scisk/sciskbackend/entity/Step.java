package com.scisk.sciskbackend.entity;

import java.util.List;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Step {
    private Long id;
    private String name;
    private String description;
    private Integer order;
    private Boolean enabled;
    private Service service;
    private List<Job> jobs;
}
