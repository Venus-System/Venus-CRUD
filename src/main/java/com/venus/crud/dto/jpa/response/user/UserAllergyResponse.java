package com.venus.crud.dto.jpa.response.user;

import com.venus.crud.entity.enums.RiskLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record UserAllergyResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Identificador da alergia.", example = "12")
        Long allergyId,
        @Schema(description = "Gravidade do alerta gerado pela regra.")
        RiskLevel severity,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
