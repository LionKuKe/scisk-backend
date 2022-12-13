package com.scisk.sciskbackend.controller;

import com.scisk.sciskbackend.dto.PaymentCreateDto;
import com.scisk.sciskbackend.dto.PaymentReturnDto;
import com.scisk.sciskbackend.service.AdvertisementService;
import com.scisk.sciskbackend.service.PaymentService;
import com.scisk.sciskbackend.util.Util;
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

    /*@Operation(summary = "Modifier un paiement", tags = {"Gestion des paiements des clients"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modification réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentReturnDto.class)))),
            @ApiResponse(responseCode = "404", description = "Le paiement n'a pas été trouvé")
    })
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<PaymentReturnDto>> update(
            @Parameter(description = "Données du paiement", required = true, schema = @Schema(implementation = PaymentCreateDto.class))
            @RequestBody PaymentCreateDto paymentCreateDto,

            @Parameter(description = "Id du paiement à modifier", required = true)
            @PathVariable String id
    ) {
        Long idValue = Util.convertStringToLong(id);
        return ResponseEntity.ok(new SimpleObjectResponse<>("record.updated", advertisementService.update(idValue, paymentCreateDto)));
    }

    @Operation(summary = "Récupérer la liste de tous les paiements de l'application)", tags = {"Gestion des paiements des clients"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentReturnDto.class)))),
    })
    @GetMapping("/find-all")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<PageObjectResponse<PaymentReturnDto>> findAll(
            @Parameter(description = "La page a retourner")
            @RequestParam(value = "page", required = false) Integer page,

            @Parameter(description = "La taille de la page à retourner")
            @RequestParam(value = "size", required = false) Integer size,

            @RequestParam(value = "observation", required = false, defaultValue = "") String observation,
            @RequestParam(value = "recordId", required = false, defaultValue = "") String recordId
    ) {
        Long recordIdValue = Util.convertStringToLong(recordId);
        Page<PaymentReturnDto> pagedResult = advertisementService.findAllPaymentByFilters(page, size, observation, recordIdValue);
        PageObjectResponse<PaymentReturnDto> response = new PageObjectResponse<>();
        response.setPageStats(pagedResult, true);
        response.setItems(pagedResult.getContent());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Récupérer un paiement par son id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Données retournées avec succès"),
            @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    })
    @GetMapping("/get-by-id/{id}")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<PaymentReturnDto>> findById(@PathVariable String id) {
        Long idValue = Util.convertStringToLong(id);
        return ResponseEntity.ok(new SimpleObjectResponse<>("ok", advertisementService.findById(idValue)));
    }

    @Operation(summary = "Supprimer un paiement", tags = {"Gestion des paiements des clients"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suppression réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentReturnDto.class)))),
            @ApiResponse(responseCode = "404", description = "Le paiement n'a pas été trouvé")
    })
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<PaymentReturnDto>> delete(
            @Parameter(description = "Id du paiement à supprimer", required = true)
            @PathVariable String id
    ) {
        Long idValue = Util.convertStringToLong(id);
        advertisementService.delete(idValue);
        return ResponseEntity.ok(new SimpleObjectResponse<>("payment.deleted", null));
    }*/

}
