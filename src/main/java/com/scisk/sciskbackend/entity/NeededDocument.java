package com.scisk.sciskbackend.entity;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NeededDocument {
    private Long id;
    private String name;
    private Service service;
    private Boolean enabled;
}
