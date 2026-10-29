package com.venus.crud.controller.jpa.admin;

import com.venus.crud.dto.jpa.request.admin.AdminLoginRequest;
import com.venus.crud.dto.jpa.response.admin.AdminLoginResponse;
import com.venus.crud.exception.ErrorResponse;
import com.venus.crud.service.jpa.admin.AdminAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/admin")
@Tag(name = "Administradores")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @Operation(
            operationId = "adminLogin",
            summary = "Faz o login do administrador",
            description = "Confere o e-mail e a senha e devolve o token de acesso da área admin. Mande o token em toda "
                    + "chamada, no cabeçalho Authorization: Bearer <token>. Qualquer falha devolve o mesmo **401**, "
                    + "sem dizer se o e-mail existe.")
    @ApiResponse(responseCode = "200", description = "Login feito. Devolve o token e os dados do administrador.")
    @ApiResponse(responseCode = "401", description = "E-mail ou senha inválidos, administrador sem senha ou inativo.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        return ResponseEntity.ok(adminAuthService.login(request));
    }
}
