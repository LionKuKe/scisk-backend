package com.scisk.sciskbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateDto {
    @Schema(description = "Date du paiement", required = true)
    @NotNull
    private Instant paymentDate;

    @Schema(description = "Montant du paiement", required = true)
    @NotNull
    private Double amount;

    @Schema(description = "Observation", required = true)
    @NotBlank
    private String observation;

    @Schema(description = "Le dossier qui est payé", required = true)
    @NotNull
    private Long recordId;
}
