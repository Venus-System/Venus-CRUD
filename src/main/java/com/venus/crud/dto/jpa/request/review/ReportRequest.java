package com.venus.crud.dto.jpa.request.review;

import com.venus.crud.entity.enums.ReportStatus;
import com.venus.crud.entity.enums.ReportTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        @NotNull Long userId,
        @Schema(description = "Identificador do administrador.", example = "7")
        Long adminUserId,
        @Schema(description = "Tipo do conteúdo denunciado.")
        @NotNull ReportTargetType targetType,
        @Schema(description = "Identificador do conteúdo denunciado.", example = "88")
        @NotNull Long targetId,
        @Schema(description = "Motivo da denúncia.", example = "Avaliação com conteúdo ofensivo.")
        @NotBlank String reason,
        @Schema(description = "Situação atual do registro.")
        @NotNull ReportStatus status
) {
}
