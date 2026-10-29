package com.venus.crud.dto.jpa.patch.user;

import com.venus.crud.entity.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record UserPatchRequest(
        @Schema(description = "Nome do usuário.", example = "Ana Souza")
        String name,
        @Schema(description = "Endereço de e-mail.", example = "contato@venus.com")
        @Email String email,
        @Schema(description = "Senha de acesso.", example = "SenhaForte123")
        String password,
        @Schema(description = "Situação atual do registro.")
        UserStatus status,
        @Schema(description = "Data e hora do último acesso.", example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime lastLogin,
        @Schema(description = "Latitude aproximada, de -90 a 90. Casas além da segunda são arredondadas.",
                example = "-23.55")
        @DecimalMin("-90") @DecimalMax("90") BigDecimal latitude,
        @Schema(description = "Longitude aproximada, de -180 a 180. Casas além da segunda são arredondadas.",
                example = "-46.63")
        @DecimalMin("-180") @DecimalMax("180") BigDecimal longitude,
        @Schema(description = "Cidade da localização aproximada, com até 100 caracteres.", example = "São Paulo")
        @Size(max = 100) String city,
        @Schema(description = "Estado da localização aproximada, com até 100 caracteres.", example = "São Paulo")
        @Size(max = 100) String state
) {
}
