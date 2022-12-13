package com.scisk.sciskbackend.entity;

import lombok.*;
import org.bson.types.Binary;

import java.time.Instant;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Advertisement {
    private Long id;
    private String title;
    private String description;
    private Binary content;
    private Instant createdAt;
    private Integer priority;
    private Boolean enabled;
}
