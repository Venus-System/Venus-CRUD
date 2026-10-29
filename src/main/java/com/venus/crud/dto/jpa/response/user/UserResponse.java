package com.venus.crud.dto.jpa.response.user;

import com.venus.crud.entity.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record UserResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador do usuário no Firebase Authentication.", example = "K3jd9SmQ2xYbN7pLw0Rt")
        String firebaseUid,
        @Schema(description = "Nome do usuário.", example = "Ana Souza")
        String name,
        @Schema(description = "Endereço de e-mail.", example = "contato@venus.com")
        String email,
        @Schema(description = "Situação atual do registro.")
        UserStatus status,
        @Schema(description = "Data e hora do último acesso.", example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime lastLogin,
        @Schema(description = "Latitude aproximada, com 2 casas decimais. Nulo quando o usuário não enviou a localização.",
                example = "-23.55")
        BigDecimal latitude,
        @Schema(description = "Longitude aproximada, com 2 casas decimais. Nulo quando o usuário não enviou a localização.",
                example = "-46.63")
        BigDecimal longitude,
        @Schema(description = "Cidade da localização aproximada.", example = "São Paulo")
        String city,
        @Schema(description = "Estado da localização aproximada.", example = "São Paulo")
        String state,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
