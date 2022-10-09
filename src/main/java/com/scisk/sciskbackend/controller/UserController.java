package com.scisk.sciskbackend.controller;

import com.scisk.sciskbackend.config.springsecurity.*;
import com.scisk.sciskbackend.dto.*;
import com.scisk.sciskbackend.datasourceentity.RefreshToken;
import com.scisk.sciskbackend.responses.JwtResponse;
import com.scisk.sciskbackend.responses.ResponseModel;
import com.scisk.sciskbackend.service.UserService;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("user/")
@Tag(name = "Gestion des utilisateurs", description = "Endpoints pour gestion des client, assistants et chefs")
public class UserController {

    private final UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Créer un compte client", tags = {"Gestion des utilisateurs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Création réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserReturnDto.class)))),
            @ApiResponse(responseCode = "409", description = "L'email déja utilisée"),
            @ApiResponse(responseCode = "412", description = "Email incorrect ou mot de passe incorrect")
    })
    @PostMapping("/create-customer-account")
    public ResponseEntity<SimpleObjectResponse<UserReturnDto>> createCustomerAccount(
            @Parameter(description = "Données du compte client", required = true, schema = @Schema(implementation = CustomerCreateDto.class))
            @RequestBody CustomerCreateDto customerCreateDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SimpleObjectResponse<>("customer.created", userService.createCustomerAccount(customerCreateDto)));
    }

    @Operation(summary = "Créer un compte employé", tags = {"Gestion des utilisateurs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Création réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserReturnDto.class)))),
            @ApiResponse(responseCode = "409", description = "L'email déja utilisée"),
            @ApiResponse(responseCode = "412", description = "Email incorrect ou mot de passe incorrect")
    })
    @PostMapping("/create-employee-account")
    public ResponseEntity<SimpleObjectResponse<UserReturnDto>> createEmployeeAccount(
            @Parameter(description = "Données du compte employé", required = true, schema = @Schema(implementation = EmployeeCreateDto.class))
            @RequestBody EmployeeCreateDto employeeCreateDto
    ) {
        return new ResponseModel<>(
                new SimpleObjectResponse<>(
                        "employee.created",
                        userService.createEmployeeAccount(employeeCreateDto)
                ),
                HttpStatus.OK
        );
    }

    @Operation(summary = "Connecter un utilisateur",
            description = "Vérifier la validité du token reçu, vérifier que les credentials qu'il contient appartiennent à un user en bd, " +
                    "et transmettre le token et le refresh token",
            tags = {"Gestion des utilisateurs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conexion réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = JwtResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrects"),
    })
    @PostMapping("/authenticate")
    public ResponseEntity<SimpleObjectResponse<JwtResponse>> authenticateUser(
            @Parameter(description = "Credentials", required = true, schema = @Schema(implementation = UserAuthenticateDto.class))
            @Valid @RequestBody UserAuthenticateDto userAuthenticateDto
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userAuthenticateDto.getEmail(), userAuthenticateDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        return new ResponseModel<>(
                new SimpleObjectResponse<>(
                        "user.connected",
                        new JwtResponse(jwt, refreshToken.getToken())
                ),
                HttpStatus.OK
        );
    }

    @Operation(summary = "Obtenir un nouveau token à partir du refresh token",
            description = "Vérifier que le refresh token est dans la bd, vérifier sa date d'expiration, générer le token",
            tags = {"Gestion des utilisateurs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token généré avec succès", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TokenRefreshResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Refresh token pas en bd ou refresh token expiré"),
    })
    @PostMapping("/refreshtoken")
    public ResponseEntity<SimpleObjectResponse<TokenRefreshResponse>> refreshtoken(
            @Parameter(description = "Credentials", required = true, schema = @Schema(implementation = TokenRefreshRequest.class))
            @Valid @RequestBody TokenRefreshRequest request
    ) {
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(
                            user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()),
                            user.getFirstname(),
                            user.getLastname(),
                            user.getEmail()

                    );
                    return new ResponseModel<>(new SimpleObjectResponse<>("token.generated", new TokenRefreshResponse(token, requestRefreshToken)), HttpStatus.OK);
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "refreshtoken.not.in.database"));
    }

    @Operation(summary = "Déconnecter un utilisateur", tags = {"Gestion des utilisateurs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Déconnexion réussie")
    })
    @PostMapping("/logout")
    public ResponseEntity<OperationResponse> LogoutUser(
            @Parameter(description = "Username", required = true)
            @RequestBody StringDto username
    ) {
        return new ResponseModel<>(new OperationResponse("user.logout"), HttpStatus.OK);
    }

    @Operation(summary = "Récupérer la liste des utilisateurs au format paginé", tags = {"Gestion des utilisateurs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserReturnDto.class)))),
    })
    @GetMapping("/all-by-filters")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<PageObjectResponse<UserReturnDto>> findAllByFilter(
            @Parameter(description = "La page a retourner")
            @RequestParam(value = "page", required = false) Integer page,

            @Parameter(description = "La taille de la page à retourner")
            @RequestParam(value = "size", required = false) Integer size,

            @RequestParam(value = "firstname", required = false, defaultValue = "") String firstname,
            @RequestParam(value = "lastname", required = false, defaultValue = "") String lastname,
            @RequestParam(value = "email", required = false, defaultValue = "") String email,
            @RequestParam(value = "role", required = false, defaultValue = "") String role,
            @RequestParam(value = "status", required = false, defaultValue = "") String status,
            @RequestParam(value = "city", required = false, defaultValue = "") String city,
            @RequestParam(value = "address", required = false, defaultValue = "") String address,
            @RequestParam(value = "country", required = false, defaultValue = "") String country
    ) {
        Page<UserReturnDto> pagedResult = userService.findAllUserByFilters(page, size, firstname, lastname, email, role, status, city, address, country);
        PageObjectResponse<UserReturnDto> response = new PageObjectResponse<>();
        response.setPageStats(pagedResult, true);
        response.setItems(pagedResult.getContent());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Modifier un utilisateur", tags = {"Gestion des utilisateurs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modification réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserReturnDto.class)))),
            @ApiResponse(responseCode = "409", description = "L'email est déja utilisée"),
            @ApiResponse(responseCode = "412", description = "Email incorrect ou mot de passe incorrect"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non retrouvé en bd par son id")
    })
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<UserReturnDto>> update(
            @Parameter(description = "Données de l'utilisateur", required = true, schema = @Schema(implementation = UserUpdateDto.class))
            @RequestBody UserUpdateDto userUpdateDto,

            @Parameter(description = "Id de l'utilisateur à modifier", required = true)
            @PathVariable String id
    ) {
        Long userIdValue = Util.convertStringToLong(id);
        return ResponseEntity.ok(new SimpleObjectResponse<>("user.updated", userService.update(userIdValue, userUpdateDto)));
    }

    @Operation(summary = "Récupérer les données d'un utilisateur par son id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Données retournées avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non connecté")
    })
    @GetMapping("/get-by-id/{id}")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<UserReturnDto>> findById(@PathVariable String id) {
        Long idValue = Util.convertStringToLong(id);
        return ResponseEntity.ok(new SimpleObjectResponse<>("ok", userService.findById(idValue)));
    }

    @Operation(summary = "Récupérer les données de l'utilisateur connecté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Données retournées avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non connecté")
    })
    @GetMapping("/get-connected-user")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<SimpleObjectResponse<UserReturnDto>> getConnectedUser() {
        return ResponseEntity.ok(new SimpleObjectResponse<>("ok", userService.getConnectedUser()));
    }

    @Operation(summary = "Modifier le mot de passe d'un utilisateur",
            description = "Modifier le champ password",
            tags = {"Gestion des utilisateurs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modification réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserReturnDto.class)))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non retrouvé en bd par son id"),
            @ApiResponse(responseCode = "412", description = "Mot de passe incorrect")
    })
    @PutMapping("/update-user-password/{userId}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseEntity<OperationResponse> changeUserPassword(
            @Parameter(description = "Id de l'utilisateur à modifier", required = true)
            @PathVariable String userId,

            @Parameter(description = "Le nouveau mot de passe à utiliser", required = true)
            @RequestBody StringDto password
    ) {
        Long userIdValue = Util.convertStringToLong(userId);
        userService.changeUserPassword(userIdValue, password.getValue());
        return new ResponseModel<>(new OperationResponse("password.changed"), HttpStatus.OK);
    }

}
