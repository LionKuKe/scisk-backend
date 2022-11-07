package com.scisk.sciskbackend.controller;

import com.scisk.sciskbackend.dto.*;
import com.scisk.sciskbackend.service.ServiceService;
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
@RequestMapping("service/")
@Tag(name = "Gestion des services", description = "Endpoints pour gestion des services offerts par la société")
public class ServiceController {

    @Autowired
    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @Operation(summary = "Créer un service", tags = {"Gestion des services"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Création réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ServiceReturnDto.class))))
    })
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<ServiceReturnDto>> create(
            @Parameter(description = "Données du service", required = true, schema = @Schema(implementation = ServiceCreateDto.class))
            @RequestBody ServiceCreateDto serviceCreateDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SimpleObjectResponse<>("service.created", serviceService.create(serviceCreateDto)));
    }

    @Operation(summary = "Modifier un service", tags = {"Gestion des services"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modification réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ServiceReturnDto.class)))),
            @ApiResponse(responseCode = "404", description = "Le service n'a pas été trouvé")
    })
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<ServiceReturnDto>> update(
            @Parameter(description = "Données du service", required = true, schema = @Schema(implementation = ServiceUpdateDto.class))
            @RequestBody ServiceUpdateDto serviceUpdateDto,

            @Parameter(description = "Id du service à modifier", required = true)
            @PathVariable String id
    ) {
        Long idValue = Util.convertStringToLong(id);
        return ResponseEntity.ok(new SimpleObjectResponse<>("service.updated", serviceService.update(idValue, serviceUpdateDto)));
    }

    @Operation(summary = "Récupérer la liste des services de l'application (pour l'administration)", tags = {"Gestion des services"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ServiceReturnDto.class)))),
    })
    @GetMapping("/find-all")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<PageObjectResponse<ServiceReturnDto>> findAll(
            @Parameter(description = "La page a retourner")
            @RequestParam(value = "page", required = false) Integer page,

            @Parameter(description = "La taille de la page à retourner")
            @RequestParam(value = "size", required = false) Integer size,

            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "description", required = false, defaultValue = "") String description
    ) {
        Page<ServiceReturnDto> pagedResult = serviceService.findAllServiceByFilters(page, size, name, description);
        PageObjectResponse<ServiceReturnDto> response = new PageObjectResponse<>();
        response.setPageStats(pagedResult, true);
        response.setItems(pagedResult.getContent());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Récupérer la liste des services utilisables par les clients", tags = {"Gestion des services"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ServiceReturnDto.class)))),
    })
    @GetMapping("/find-for-customer")
    public ResponseEntity<PageObjectResponse<ServiceReturnDto>> findAllForCustomers(
            @Parameter(description = "La page a retourner")
            @RequestParam(value = "page", required = false) Integer page,

            @Parameter(description = "La taille de la page à retourner")
            @RequestParam(value = "size", required = false) Integer size,

            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "description", required = false, defaultValue = "") String description
    ) {
        Page<ServiceReturnDto> pagedResult = serviceService.findAllForCustomers(page, size, name, description);
        PageObjectResponse<ServiceReturnDto> response = new PageObjectResponse<>();
        response.setPageStats(pagedResult, true);
        response.setItems(pagedResult.getContent());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Récupérer un service par son id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Données retournées avec succès"),
            @ApiResponse(responseCode = "404", description = "Service non trouvé")
    })
    @GetMapping("/get-by-id/{id}")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<ServiceReturnDto>> findById(@PathVariable String id) {
        Long idValue = Util.convertStringToLong(id);
        return ResponseEntity.ok(new SimpleObjectResponse<>("ok", serviceService.findById(idValue)));
    }

    @Operation(summary = "Créer une étape dans un service existant", tags = {"Gestion des services"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Création réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = StepReturnDto.class))))
    })
    @PostMapping("/create/{id}/step")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<StepReturnDto>> createStep(
            @Parameter(description = "Données de l'étape", required = true, schema = @Schema(implementation = StepCreateDto.class))
            @RequestBody StepCreateDto stepCreateDto,

            @Parameter(description = "Id du service dans lequel créer l'étape", required = true)
            @PathVariable String id
    ) {
        Long idValue = Util.convertStringToLong(id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SimpleObjectResponse<>("step.created", serviceService.createStep(idValue, stepCreateDto)));
    }

    @Operation(summary = "Créer un document dans un service existant", tags = {"Gestion des services"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Création réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = NeededDocumentReturnDto.class))))
    })
    @PostMapping("/create/{id}/needed-document")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<NeededDocumentReturnDto>> createNeededDocument(
            @Parameter(description = "Données du document", required = true, schema = @Schema(implementation = NeededDocumentCreateDto.class))
            @RequestBody NeededDocumentCreateDto neededDocumentCreateDto,

            @Parameter(description = "Id du service dans lequel créer le document", required = true)
            @PathVariable String id
    ) {
        Long idValue = Util.convertStringToLong(id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SimpleObjectResponse<>("neededDocument.created", serviceService.createNeededDocument(idValue, neededDocumentCreateDto)));
    }

    @Operation(summary = "Modifier une étape dans un service", tags = {"Gestion des services"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modification réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = StepReturnDto.class)))),
            @ApiResponse(responseCode = "404", description = "Le service ou l'étape n'ont pas été trouvés")
    })
    @PutMapping("/update/{id}/step/{stepId}")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<StepReturnDto>> updateStep(
            @Parameter(description = "Données de l'étape à modifier", required = true, schema = @Schema(implementation = StepCreateDto.class))
            @RequestBody StepCreateDto stepCreateDto,

            @Parameter(description = "Id du service à modifier", required = true)
            @PathVariable String id,

            @Parameter(description = "Id de l'étape à modifier", required = true)
            @PathVariable String stepId
    ) {
        Long idValue = Util.convertStringToLong(id);
        Long stepIdValue = Util.convertStringToLong(stepId);
        return ResponseEntity.ok(new SimpleObjectResponse<>("step.updated", serviceService.updateStep(idValue, stepIdValue, stepCreateDto)));
    }

    @Operation(summary = "Modifier un document dans un service", tags = {"Gestion des services"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modification réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = NeededDocumentReturnDto.class)))),
            @ApiResponse(responseCode = "404", description = "Le service ou l'étape n'ont pas été trouvés")
    })
    @PutMapping("/update/{id}/needed-document/{neededDocumentId}")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<NeededDocumentReturnDto>> updateNeededDocument(
            @Parameter(description = "Données de l'étape à modifier", required = true, schema = @Schema(implementation = NeededDocumentCreateDto.class))
            @RequestBody NeededDocumentCreateDto neededDocumentCreateDto,

            @Parameter(description = "Id du service à modifier", required = true)
            @PathVariable String id,

            @Parameter(description = "Id du document à modifier", required = true)
            @PathVariable String neededDocumentId
    ) {
        Long idValue = Util.convertStringToLong(id);
        Long neededDocumentIdValue = Util.convertStringToLong(neededDocumentId);
        return ResponseEntity.ok(new SimpleObjectResponse<>("neededDocument.updated", serviceService.updateNeededDocument(idValue, neededDocumentIdValue, neededDocumentCreateDto)));
    }

}
