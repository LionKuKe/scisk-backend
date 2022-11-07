package com.scisk.sciskbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    @Schema(description = "Prénom du client", required = true)
    @NotBlank
    private String firstname;

    @Schema(description = "Nom du client", required = true)
    @NotBlank
    private String lastname;

    @Schema(description = "Email du client", required = true)
    @NotBlank
    private String email;

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

    @Schema(description = "Booléen indiquant si l'utilisateur est un employé ou un client", required = true)
    @NotNull
    private Boolean employee;

    @Schema(description = "Les roles de l'utilisateur : CUSTOMER, ASSISTANT, CHIEF, ADMINISTRATOR", required = true)
    @NotNull
    private List<String> roles;
}
