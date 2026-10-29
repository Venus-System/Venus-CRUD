package com.venus.crud.dto.jpa.patch.user;

import com.venus.crud.entity.enums.RiskLevel;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserAllergyPatchRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Identificador da alergia.", example = "12")
        Long allergyId,
        @Schema(description = "Gravidade do alerta gerado pela regra.")
        RiskLevel severity
) {
}
