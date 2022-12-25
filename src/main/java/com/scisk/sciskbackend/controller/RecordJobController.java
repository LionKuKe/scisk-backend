package com.scisk.sciskbackend.controller;

import com.scisk.sciskbackend.dto.AssignRecordJobToEmployeeDto;
import com.scisk.sciskbackend.dto.CloseRecordJobByAssistantDto;
import com.scisk.sciskbackend.dto.CloseRecordJobByChiefDto;
import com.scisk.sciskbackend.service.RecordJobService;
import com.scisk.sciskbackend.util.Util;
import com.scisk.sciskbackend.util.response.OperationResponse;
import com.scisk.sciskbackend.util.response.SimpleObjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("record-job/")
@Tag(name = "Gestion des taches de dossier", description = "Endpoints pour gestion des taches d'un dossier")
public class RecordJobController {

    @Autowired
    private final RecordJobService recordJobService;

    public RecordJobController(RecordJobService recordJobService) {
        this.recordJobService = recordJobService;
    }

    @Operation(summary = "Affecter une tache à un employé", tags = {"Gestion des taches de dossier"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Affectation réussie"),
            @ApiResponse(responseCode = "404", description = "La tache ou l'employé n'ont pas été trouvés")
    })
    @PutMapping("/assign-record-job/{id}/to-employee/{userId}")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<OperationResponse> assignRecordJobToEmployee(

            @Parameter(description = "Id de la tache à modifier", required = true)
            @PathVariable String id,

            @Parameter(description = "Id de de l'employé", required = true)
            @PathVariable String userId,

            @Parameter(description = "Données de l'affectation", required = true, schema = @Schema(implementation = AssignRecordJobToEmployeeDto.class))
            @RequestBody AssignRecordJobToEmployeeDto assignRecordJobToEmployeeDto
    ) {
        Long idValue = Util.convertStringToLong(id);
        Long userIdValue = Util.convertStringToLong(userId);
        recordJobService.assignRecordJobToEmployee(idValue, userIdValue, assignRecordJobToEmployeeDto);
        return ResponseEntity.ok(new SimpleObjectResponse<>("recordJob.assigned", null));
    }

    @Operation(summary = "Cloturer ou marquer une tache comme réalisée par l'assistant", tags = {"Gestion des taches de dossier"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modification réussie"),
            @ApiResponse(responseCode = "404", description = "La tache ou l'employé n'ont pas été trouvés")
    })
    @PutMapping("/close-record-job/{id}/by-assistant")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<OperationResponse> closeRecordJobByAssistant(

            @Parameter(description = "Id de la tache à modifier", required = true)
            @PathVariable String id,

            @Parameter(description = "Données de de cloture", required = true, schema = @Schema(implementation = CloseRecordJobByAssistantDto.class))
            @RequestBody CloseRecordJobByAssistantDto closeRecordJobByEmployeeDto
    ) {
        Long idValue = Util.convertStringToLong(id);
        recordJobService.closeRecordJobByAssistant(idValue, closeRecordJobByEmployeeDto);
        return ResponseEntity.ok(new SimpleObjectResponse<>("recordJob.closed", null));
    }

    @Operation(summary = "Cloturer ou marquer une tache comme réalisée par le chef (pour valider la cloture de l'employé)",
            description = "Si la tache est la dernière de l'étape, le logiciel marque l'étape comme validée en y incluant la date de cloture",
            tags = {"Gestion des taches de dossier"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modification réussie"),
            @ApiResponse(responseCode = "404", description = "La tache ou l'employé n'ont pas été trouvés")
    })
    @PutMapping("/close-record-job/{id}/by-chief")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<OperationResponse> closeRecordJobByChief(

            @Parameter(description = "Id de la tache à modifier", required = true)
            @PathVariable String id,

            @Parameter(description = "Données de de cloture", required = true, schema = @Schema(implementation = CloseRecordJobByChiefDto.class))
            @RequestBody CloseRecordJobByChiefDto closeRecordJobByChiefDto
    ) {
        Long idValue = Util.convertStringToLong(id);
        recordJobService.closeRecordJobByChief(idValue, closeRecordJobByChiefDto);
        return ResponseEntity.ok(new SimpleObjectResponse<>("recordJob.closed", null));
    }

}
