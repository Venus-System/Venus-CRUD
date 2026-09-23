package com.venus.crud.dto.mongo.response;

import com.venus.crud.entity.enums.AnalysisStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record ScanSessionResponse(
        @Schema(description = "Identificador do registro.", example = "665f1b2c9a4e3b0012ab34cd")
        String id,
        @Schema(description = "Situação atual do registro.")
        AnalysisStatus status,
        @Schema(description = "Aparelho que fez o scan.")
        ScanDeviceResponse device,
        @Schema(description = "Data e hora de início.", example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime startedAt,
        @Schema(description = "Data e hora em que o processamento terminou.", example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime finishedAt,
        @Schema(description = "Checagem de qualidade da foto do scan.")
        ScanQualityCheckResponse qualityCheck,
        @Schema(description = "Texto completo extraído do rótulo pelo OCR.",
                example = "AQUA, SODIUM LAURETH SULFATE, COCAMIDOPROPYL BETAINE")
        String fullOcrText,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
