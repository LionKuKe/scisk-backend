package com.scisk.sciskbackend.controller;

import com.scisk.sciskbackend.config.springsecurity.*;
import com.scisk.sciskbackend.dto.UserAuthenticateDto;
import com.scisk.sciskbackend.dto.UserCreateDto;
import com.scisk.sciskbackend.dto.UserReturnDto;
import com.scisk.sciskbackend.entity.RefreshToken;
import com.scisk.sciskbackend.responses.JwtResponse;
import com.scisk.sciskbackend.responses.OperationResponseModel;
import com.scisk.sciskbackend.responses.ResponseModel;
import com.scisk.sciskbackend.responses.SimpleObjectResponseModel;
import com.scisk.sciskbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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

    @Operation(summary = "Créer un compte utilisateur", tags = {"Gestion des utilisateurs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Création réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserReturnDto.class)))),
            @ApiResponse(responseCode = "409", description = "L'email déja utilisée"),
            @ApiResponse(responseCode = "412", description = "Email incorrect ou mot de passe incorrect")
    })
    @PostMapping("/create-account")
    public ResponseModel createUserAccount(
            @Parameter(description = "Données du compte client", required = true, schema = @Schema(implementation = UserCreateDto.class))
            @RequestBody UserCreateDto userCreateDto
    ) {
        return new ResponseModel<>(
                new SimpleObjectResponseModel<>(
                        "account.created",
                        userService.createAccount(userCreateDto)
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
    public ResponseModel authenticateUser(
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
                new SimpleObjectResponseModel<>(
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
    public ResponseModel refreshtoken(
            @Parameter(description = "Credentials", required = true, schema = @Schema(implementation = TokenRefreshRequest.class))
            @Valid @RequestBody TokenRefreshRequest request
    ) {
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(
                            user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList()),
                            user.getFirstname(),
                            user.getLastname(),
                            user.getEmail()

                    );
                    return new ResponseModel<>(new SimpleObjectResponseModel<>("token.generated", new TokenRefreshResponse(token, requestRefreshToken)), HttpStatus.OK);
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "refreshtoken.not.in.database"));
    }

    @Operation(summary = "Déconnecter un utilisateur", tags = {"Gestion des utilisateurs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Déconnexion réussie")
    })
    @PostMapping("/logout")
    public ResponseModel LogoutUser(
            @Parameter(description = "Username", required = true)
            @RequestBody String username
    ) {
        return new ResponseModel<>(new OperationResponseModel("user.logout"), HttpStatus.OK);
    }

    /*@Operation(summary = "Récupérer la liste des utilisateurs au format paginé", tags = {"Gestion des utilisateurs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserReturnDto.class)))),
    })
    @GetMapping("/all-by-filters")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseModel findAllByFilter(
            @Parameter(description = "La page a retourner")
            @RequestParam(value = "page", required = false) Integer page,

            @Parameter(description = "La taille de la page à retourner")
            @RequestParam(value = "size", required = false) Integer size,

            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "email", required = false, defaultValue = "") String email,
            @RequestParam(value = "role", required = false, defaultValue = "") String role,
            @RequestParam(value = "status", required = false, defaultValue = "") String status
    ) {
        return userInputBoundaryInterface.findAllUserByFilters(
                page, size, name, email, role, status
        );
    }

    @Operation(summary = "Modifier un utilisateur",
            description = "Modifier les champs : nom, email et role",
            tags = {"Gestion des utilisateurs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modification réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserReturnDto.class)))),
            @ApiResponse(responseCode = "409", description = "L'email est déja utilisée"),
            @ApiResponse(responseCode = "412", description = "Email incorrect"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non retrouvé en bd par son id")
    })
    @PutMapping("/update-user/{userId}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseModel updateUser(
            @Parameter(description = "Données du compte client", required = true, schema = @Schema(implementation = UserUpdateDto.class))
            @RequestBody UserUpdateDto userUpdateDto,

            @Parameter(description = "Id de l'utilisateur à modifier", required = true)
            @PathVariable String userId
    ) {
        Long userIdValue = Util.convertStringToLong(userId);
        return userInputBoundaryInterface.updateUser(userIdValue, userUpdateDto);
    }

    @Operation(summary = "Activer ou  désactiver un utilisateur",
            description = "Modifier le champ status",
            tags = {"Gestion des utilisateurs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modification réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserReturnDto.class)))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non retrouvé en bd par son id ou statut non existant en bd")
    })
    @PutMapping("/change-user-status/{userId}/{status}")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseModel changeUserStatus(
            @Parameter(description = "Id de l'utilisateur à activer/désactiver", required = true)
            @PathVariable String userId,

            @Parameter(description = "Le nouveau statut à utiliser", required = true)
            @PathVariable String status
    ) {
        Long userIdValue = Util.convertStringToLong(userId);
        return userInputBoundaryInterface.changeUserStatus(userIdValue, status);
    }

    @Operation(summary = "Modifier le mot de passe d'un utilisateur",
            description = "Modifier le champ password",
            tags = {"Gestion des utilisateurs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modification réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserReturnDto.class)))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non retrouvé en bd par son id"),
            @ApiResponse(responseCode = "412", description = "Mot de passe invalide")
    })
    @PutMapping("/update-user-password/{userId}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER', 'ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseModel changeUserPassword(
            @Parameter(description = "Id de l'utilisateur à modifier", required = true)
            @PathVariable String userId,

            @Parameter(description = "Le nouveau mot de passe à utiliser", required = true)
            @RequestBody String password
    ) {
        Long userIdValue = Util.convertStringToLong(userId);
        return userInputBoundaryInterface.changeUserPassword(userIdValue, password);
    }

    @Operation(summary = "Supprimer un utilisateur",
            description = "Supprimer un utilisateur",
            tags = {"Gestion des utilisateurs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suppression réussie"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non retrouvé en bd par son id")
    })
    @DeleteMapping("/delete-user/{userId}")
    @PreAuthorize("hasAnyAuthority('ASSISTANT', 'CHIEF', 'ADMINISTRATOR')")
    public ResponseModel deleteUser(
            @Parameter(description = "Id de l'utilisateur à supprimer", required = true)
            @PathVariable String userId
    ) {
        Long userIdValue = Util.convertStringToLong(userId);
        return userInputBoundaryInterface.deleteUser(userIdValue);
    }

    @Operation(summary = "Créer compte assistant ou administrateur", tags = {"Gestion des utilisateurs"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Création réussie", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserCreateAccountDto.class)))),
            @ApiResponse(responseCode = "409", description = "L'email déja utilisée"),
            @ApiResponse(responseCode = "412", description = "Email incorrect ou mot de passe incorrect")
    })
    @PostMapping("/create-user-account")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR')")
    public ResponseModel createUserAccount(
            @Parameter(description = "Données du compte utiisateur", required = true, schema = @Schema(implementation = UserCreateAccountDto.class))
            @RequestBody UserCreateAccountDto userCreateAccountDto
    ) {
        return userInputBoundaryInterface.createUserAccount(userCreateAccountDto);
    }*/

}
