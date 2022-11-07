package com.scisk.sciskbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordCreateDto {
    @Schema(description = "Le client du dossier", required = true)
    @NotNull
    private Long customerId;

    @Schema(description = "Le service choisi", required = true)
    @NotBlank
    private Long serviceId;
}
