package com.scisk.sciskbackend.controller;

import com.scisk.sciskbackend.dto.AdvertisementListDto;
import com.scisk.sciskbackend.dto.PaymentCreateDto;
import com.scisk.sciskbackend.dto.PaymentReturnDto;
import com.scisk.sciskbackend.service.AdvertisementService;
import com.scisk.sciskbackend.service.PaymentService;
import com.scisk.sciskbackend.util.Util;
import com.scisk.sciskbackend.util.response.ListObjectResponse;
import com.scisk.sciskbackend.util.response.OperationResponse;
import com.scisk.sciskbackend.util.response.PageObjectResponse;
import com.scisk.sciskbackend.util.response.SimpleObjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@RestController()
@RequestMapping("advertisement/")
@Tag(name = "Gestion des annonces", description = "Endpoints pour la gestion des annonces dans l'application")
public class AdvertisementController {

    @Autowired
    private final AdvertisementService advertisementService;

    public AdvertisementController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    @Operation(summary = "Créer une annonce", tags = {"Gestion des annonces"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Création réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentReturnDto.class))))
    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<OperationResponse> create(
            @NotNull @RequestParam("file") MultipartFile file,
            @NotBlank @RequestParam("title") String title,
            @NotBlank @RequestParam("description") String description,
            @NotNull @RequestParam("priority") Integer priority
    ) {
        advertisementService.create(file, title, description, priority);
        return ResponseEntity.ok(new OperationResponse("advertisement.created"));
    }

    @Operation(summary = "Récupérer la liste des annonces ordonnée par ordre de priorité", tags = {"Gestion des annonces"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    })
    @GetMapping("/find-all-enabled")
    public ResponseEntity<ListObjectResponse<AdvertisementListDto>> findAllEnabled() {
        return ResponseEntity.ok(new ListObjectResponse<>("", advertisementService.findAllEnabled()));
    }

}
