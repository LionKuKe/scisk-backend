package com.scisk.sciskbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloseRecordJobByAssistantDto {
    private Instant startDate;
    private Instant endDate;
    private String observation;
}
