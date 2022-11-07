package com.scisk.sciskbackend.controller;

import com.scisk.sciskbackend.dto.PaymentCreateDto;
import com.scisk.sciskbackend.dto.PaymentReturnDto;
import com.scisk.sciskbackend.service.PaymentService;
import com.scisk.sciskbackend.util.Util;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("payment/")
@Tag(name = "Gestion des paiements des clients", description = "Endpoints pour gestion des paiements des dossiers")
public class PaymentController {

    @Autowired
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Créer un paiement sur un dossier", tags = {"Gestion des paiements des clients"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Création réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentReturnDto.class))))
    })
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<PaymentReturnDto>> create(
            @Parameter(description = "Données du paiement", required = true, schema = @Schema(implementation = PaymentCreateDto.class))
            @RequestBody PaymentCreateDto paymentCreateDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SimpleObjectResponse<>("payment.created", paymentService.create(paymentCreateDto)));
    }

    @Operation(summary = "Modifier un paiement", tags = {"Gestion des paiements des clients"})
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
        return ResponseEntity.ok(new SimpleObjectResponse<>("record.updated", paymentService.update(idValue, paymentCreateDto)));
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
        Page<PaymentReturnDto> pagedResult = paymentService.findAllPaymentByFilters(page, size, observation, recordIdValue);
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
        return ResponseEntity.ok(new SimpleObjectResponse<>("ok", paymentService.findById(idValue)));
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
        paymentService.delete(idValue);
        return ResponseEntity.ok(new SimpleObjectResponse<>("payment.deleted", null));
    }

}
