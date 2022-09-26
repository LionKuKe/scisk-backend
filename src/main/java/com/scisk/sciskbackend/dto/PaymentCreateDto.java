package com.scisk.sciskbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateDto {
    @Schema(description = "Nom du service", required = true)
    @NotBlank
    private String name;

    @Schema(description = "Description du service", required = true)
    @NotBlank
    private String description;

    @Schema(description = "Etat du service : activé ou désactivé", required = true)
    @NotNull
    @Builder.Default
    private Boolean enabled = true;

    @Schema(description = "Les étapes du service", required = true)
    @NotNull
    @Builder.Default
    private List<StepCreateDto> steps = new ArrayList<>();

    @Schema(description = "Les documents nécessaires à la création du service")
    @Builder.Default
    private List<NeededDocumentCreateDto> neededDocuments = new ArrayList<>();
}
