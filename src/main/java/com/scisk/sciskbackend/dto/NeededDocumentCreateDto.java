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
public class NeededDocumentCreateDto {
    @Schema(description = "Nom du document", required = true)
    @NotBlank
    private String name;

    @Schema(description = "Etat du document : activé ou désactivé", required = true)
    @NotNull
    @Builder.Default
    private Boolean enabled = true;
}
