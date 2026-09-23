package com.venus.crud.dto.jpa.patch.review;

import com.venus.crud.entity.enums.ReportStatus;
import com.venus.crud.entity.enums.ReportTargetType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ReportPatchRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Identificador do administrador.", example = "7")
        Long adminUserId,
        @Schema(description = "Tipo do conteúdo denunciado.")
        ReportTargetType targetType,
        @Schema(description = "Identificador do conteúdo denunciado.", example = "88")
        Long targetId,
        @Schema(description = "Motivo da denúncia.", example = "Avaliação com conteúdo ofensivo.")
        String reason,
        @Schema(description = "Situação atual do registro.")
        ReportStatus status
) {
}
