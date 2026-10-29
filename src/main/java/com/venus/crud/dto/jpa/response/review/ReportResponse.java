package com.venus.crud.dto.jpa.response.review;

import com.venus.crud.entity.enums.ReportStatus;
import com.venus.crud.entity.enums.ReportTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record ReportResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
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
        ReportStatus status,
        @Schema(description = "Data e hora em que a denúncia foi tratada.", example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime handledAt,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
