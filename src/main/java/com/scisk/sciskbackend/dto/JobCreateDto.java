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
public class JobCreateDto {

    @Schema(description = "Le nom de la tache", required = true)
    @NotBlank
    private String name;

    private String description;

    @Schema(description = "Le numéro d'ordre de la tache", required = true)
    @NotNull
    private Integer order;

    @Schema(description = "L'id de l'étape", required = true)
    @NotBlank
    private Long stepId;
}
