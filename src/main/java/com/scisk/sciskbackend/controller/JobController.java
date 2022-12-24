package com.scisk.sciskbackend.controller;

import com.scisk.sciskbackend.dto.*;
import com.scisk.sciskbackend.service.JobService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("job/")
@Tag(name = "Gestion des taches", description = "Endpoints pour gestion des taches (configurées sur les étapes")
public class JobController {

    @Autowired
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @Operation(summary = "Créer une tache", tags = {"Gestion des taches"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Création réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = JobReturnDto.class))))
    })
    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<JobReturnDto>> create(
            @Parameter(description = "Données de la tache", required = true, schema = @Schema(implementation = JobCreateDto.class))
            @RequestBody JobCreateDto jobCreateDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SimpleObjectResponse<>("job.created", jobService.create(jobCreateDto)));
    }

    @Operation(summary = "Modifier une tache", tags = {"Gestion des taches"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modification réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = JobReturnDto.class)))),
            @ApiResponse(responseCode = "404", description = "La tache à modifier n'a pas été trouvée")
    })
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<JobReturnDto>> update(
            @Parameter(description = "Données de la tache", required = true, schema = @Schema(implementation = JobUpdateDto.class))
            @RequestBody JobUpdateDto jobUpdateDto,

            @Parameter(description = "Id de la tache à modifier", required = true)
            @PathVariable String id
    ) {
        Long idValue = Util.convertStringToLong(id);
        return ResponseEntity.ok(new SimpleObjectResponse<>("job.updated", jobService.update(idValue, jobUpdateDto)));
    }

    @Operation(summary = "Récupérer la liste des taches de l'application (pour l'administration)", tags = {"Gestion des taches"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = JobReturnDto.class)))),
    })
    @GetMapping("/find-all")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<PageObjectResponse<JobReturnDto>> findAll(
            @Parameter(description = "La page a retourner")
            @RequestParam(value = "page", required = false) Integer page,

            @Parameter(description = "La taille de la page à retourner")
            @RequestParam(value = "size", required = false) Integer size,

            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "description", required = false, defaultValue = "") String description,
            @RequestParam(value = "stepId", required = false, defaultValue = "") String stepId
    ) {
        Long stepIdValue = Util.convertStringToLong(stepId);
        Page<JobReturnDto> pagedResult = jobService.findAllByFilters(page, size, name, description, stepIdValue);
        PageObjectResponse<JobReturnDto> response = new PageObjectResponse<>();
        response.setPageStats(pagedResult, true);
        response.setItems(pagedResult.getContent());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Récupérer une tache par son id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Données retournées avec succès"),
            @ApiResponse(responseCode = "404", description = "Tache non trouvée")
    })
    @GetMapping("/get-by-id/{id}")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<JobReturnDto>> findById(@PathVariable String id) {
        Long idValue = Util.convertStringToLong(id);
        return ResponseEntity.ok(new SimpleObjectResponse<>("ok", jobService.findById(idValue)));
    }

    @Operation(summary = "Supprimer une tache", tags = {"Gestion des taches"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suppression réussie"),
            @ApiResponse(responseCode = "404", description = "La tache n'a pas été trouvée"),
            @ApiResponse(responseCode = "409", description = "La tache ne peut être supprimée car elle est déja liée à des dossiers")
    })
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<OperationResponse> delete(
            @Parameter(description = "Id de la tache à supprimer", required = true)
            @PathVariable String id
    ) {
        Long idValue = Util.convertStringToLong(id);
        jobService.delete(idValue);
        return ResponseEntity.ok(new SimpleObjectResponse<>("job.deleted", null));
    }

}
