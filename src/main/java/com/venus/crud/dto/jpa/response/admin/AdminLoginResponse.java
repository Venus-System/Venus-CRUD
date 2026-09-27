package com.venus.crud.dto.jpa.response.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record AdminLoginResponse(
        @Schema(description = "Token de acesso da área admin. Mande em toda chamada no cabeçalho Authorization: Bearer <token>.",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ2ZW51cy1jcnVkIn0.assinatura")
        String accessToken,
        @Schema(description = "Tipo do token.", example = "Bearer")
        String tokenType,
        @Schema(description = "Data e hora em que o token deixa de valer.", example = "2026-09-26T22:30:00-03:00")
        OffsetDateTime expiresAt,
        @Schema(description = "Dados do administrador que fez o login.")
        AdminUserResponse admin
) {
}
