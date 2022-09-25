package com.scisk.sciskbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreateDto {
    @Schema(description = "Prénom du client", required = true)
    @NotBlank
    private String firstName;

    @Schema(description = "Nom du client", required = true)
    @NotBlank
    private String lastName;

    @Schema(description = "Email du client", required = true)
    @NotBlank
    private String email;

    @Schema(description = "Mot de passe du client", required = true)
    @NotBlank
    private String password;

    private String phone1;

    private String phone2;

    private String phone3;

    @Schema(description = "Pays de l'utilisateur", required = true)
    @NotBlank
    private String country;

    @Schema(description = "Ville de l'utilisateur", required = true)
    @NotBlank
    private String city;

    @Schema(description = "Addresse de l'utilisateur")
    private String address;
}
