package com.venus.crud.dto.jpa.request.user;

import com.venus.crud.entity.enums.RiskLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserAllergyRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        @NotNull Long userId,
        @Schema(description = "Identificador da alergia.", example = "12")
        @NotNull Long allergyId,
        @Schema(description = "Gravidade do alerta gerado pela regra.")
        @NotNull RiskLevel severity
) {
}
