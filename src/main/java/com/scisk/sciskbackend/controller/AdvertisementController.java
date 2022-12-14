package com.scisk.sciskbackend.controller;

import com.scisk.sciskbackend.dto.AdvertisementListDto;
import com.scisk.sciskbackend.service.AdvertisementService;
import com.scisk.sciskbackend.util.Util;
import com.scisk.sciskbackend.util.response.ListObjectResponse;
import com.scisk.sciskbackend.util.response.OperationResponse;
import com.scisk.sciskbackend.util.response.PageObjectResponse;
import com.scisk.sciskbackend.util.response.SimpleObjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
            @ApiResponse(responseCode = "201", description = "Création réussie")
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

    @Operation(summary = "Récupérer la liste de toutes les annonces de l'application)", tags = {"Gestion des annonces"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    })
    @GetMapping("/find-all")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<PageObjectResponse<AdvertisementListDto>> findAll(
            @Parameter(description = "La page a retourner")
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,

            @Parameter(description = "La taille de la page à retourner")
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,

            @RequestParam(value = "title", required = false, defaultValue = "") String title,
            @RequestParam(value = "description", required = false, defaultValue = "") String description
    ) {
        Page<AdvertisementListDto> pagedResult = advertisementService.findAll(page-1, size, title, description);
        PageObjectResponse<AdvertisementListDto> response = new PageObjectResponse<>();
        response.setPageStats(pagedResult, true);
        response.setItems(pagedResult.getContent());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Supprimer une annonce", tags = {"Gestion des annonces"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suppression réussie"),
            @ApiResponse(responseCode = "404", description = "L'annonce n'a pas été trouvée")
    })
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<OperationResponse> delete(
            @Parameter(description = "Id de l'annonce à supprimer", required = true)
            @PathVariable String id
    ) {
        Long idValue = Util.convertStringToLong(id);
        advertisementService.delete(idValue);
        return ResponseEntity.ok(new SimpleObjectResponse<>("advertisement.deleted", null));
    }

    @Operation(summary = "Modifier une annonce", tags = {"Gestion des annonces"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modification réussie"),
            @ApiResponse(responseCode = "404", description = "L'annonce à modifier n'a pas été trouvée")
    })
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<OperationResponse> update(
            @NotNull @RequestParam("file") MultipartFile file,
            @NotBlank @RequestParam("title") String title,
            @NotBlank @RequestParam("description") String description,
            @NotNull @RequestParam("priority") Integer priority,
            @NotNull @RequestParam("enabled") Boolean enabled,

            @Parameter(description = "Id de l'annonce à modifier", required = true)
            @PathVariable String id
    ) {
        Long idValue = Util.convertStringToLong(id);
        advertisementService.update(idValue, file, title, description, priority, enabled);
        return ResponseEntity.ok(new OperationResponse("advertisement.updated"));
    }

}
