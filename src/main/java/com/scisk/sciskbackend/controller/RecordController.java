package com.scisk.sciskbackend.controller;

import com.scisk.sciskbackend.dto.*;
import com.scisk.sciskbackend.service.RecordService;
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
@RequestMapping("record/")
@Tag(name = "Gestion des dossiers des clients", description = "Endpoints pour gestion des dossiers des clients de la société")
public class RecordController {

    @Autowired
    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @Operation(summary = "Créer un dossier", tags = {"Gestion des dossiers des clients"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Création réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RecordReturnDto.class))))
    })
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<RecordReturnDto>> create(
            @Parameter(description = "Données du dossier", required = true, schema = @Schema(implementation = RecordCreateDto.class))
            @RequestBody RecordCreateDto recordCreateDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SimpleObjectResponse<>("record.created", recordService.create(recordCreateDto)));
    }

    @Operation(summary = "Modifier un dossier", tags = {"Gestion des dossiers des clients"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modification réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RecordReturnDto.class)))),
            @ApiResponse(responseCode = "404", description = "Le dossier n'a pas été trouvé")
    })
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<RecordReturnDto>> update(
            @Parameter(description = "Données du dossier", required = true, schema = @Schema(implementation = RecordCreateDto.class))
            @RequestBody RecordCreateDto recordCreateDto,

            @Parameter(description = "Id du dossier à modifier", required = true)
            @PathVariable String id
    ) {
        Long idValue = Util.convertStringToLong(id);
        return ResponseEntity.ok(new SimpleObjectResponse<>("record.updated", recordService.update(idValue, recordCreateDto)));
    }

    @Operation(summary = "Récupérer la liste de tous les dossiers de l'application)", tags = {"Gestion des dossiers des clients"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RecordReturnDto.class)))),
    })
    @GetMapping("/find-all")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<PageObjectResponse<RecordReturnDto>> findAll(
            @Parameter(description = "La page a retourner")
            @RequestParam(value = "page", required = false) Integer page,

            @Parameter(description = "La taille de la page à retourner")
            @RequestParam(value = "size", required = false) Integer size,

            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "description", required = false, defaultValue = "") String description
    ) {
        Page<RecordReturnDto> pagedResult = recordService.findAllRecordByFilters(page, size, name, description);
        PageObjectResponse<RecordReturnDto> response = new PageObjectResponse<>();
        response.setPageStats(pagedResult, true);
        response.setItems(pagedResult.getContent());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Récupérer la liste des dossiers du client connecté", tags = {"Gestion des dossiers des clients"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RecordReturnDto.class)))),
    })
    @GetMapping("/find-for-customer")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<PageObjectResponse<RecordReturnDto>> findAllForCustomers(
            @Parameter(description = "La page a retourner")
            @RequestParam(value = "page", required = false) Integer page,

            @Parameter(description = "La taille de la page à retourner")
            @RequestParam(value = "size", required = false) Integer size,

            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "description", required = false, defaultValue = "") String description
    ) {
        Page<RecordReturnDto> pagedResult = recordService.findAllForCustomers(page, size, name, description);
        PageObjectResponse<RecordReturnDto> response = new PageObjectResponse<>();
        response.setPageStats(pagedResult, true);
        response.setItems(pagedResult.getContent());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Récupérer un dossier par son id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Données retournées avec succès"),
            @ApiResponse(responseCode = "404", description = "Dossier non trouvé")
    })
    @GetMapping("/get-by-id/{id}")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<RecordReturnDto>> findById(@PathVariable String id) {
        Long idValue = Util.convertStringToLong(id);
        return ResponseEntity.ok(new SimpleObjectResponse<>("ok", recordService.findById(idValue)));
    }

    @Operation(summary = "Suspendre un dossier", tags = {"Gestion des dossiers des clients"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suspension réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RecordReturnDto.class)))),
            @ApiResponse(responseCode = "404", description = "Le dossier n'a pas été trouvé")
    })
    @PutMapping("/suspend/{id}")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<RecordReturnDto>> suspend(
            @Parameter(description = "Id du dossier à suspendre", required = true)
            @PathVariable String id
    ) {
        Long idValue = Util.convertStringToLong(id);
        return ResponseEntity.ok(new SimpleObjectResponse<>("record.suspended", recordService.suspend(idValue)));
    }

}
