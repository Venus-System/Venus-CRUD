package com.venus.crud.dto.mongo.request;

import com.venus.crud.entity.enums.AnalysisStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record ScanSessionRequest(
        @Schema(description = "Situação atual do registro.")
        @NotNull AnalysisStatus status,
        @Schema(description = "Aparelho que fez o scan.")
        @NotNull @Valid ScanDeviceRequest device,
        @Schema(description = "Data e hora de início.", example = "2026-09-22T14:30:00-03:00")
        @NotNull OffsetDateTime startedAt,
        @Schema(description = "Data e hora em que o processamento terminou.", example = "2026-09-22T14:30:00-03:00")
        @NotNull OffsetDateTime finishedAt,
        @Schema(description = "Checagem de qualidade da foto do scan.")
        @NotNull @Valid ScanQualityCheckRequest qualityCheck,
        @Schema(description = "Texto completo extraído do rótulo pelo OCR.",
                example = "AQUA, SODIUM LAURETH SULFATE, COCAMIDOPROPYL BETAINE")
        String fullOcrText
) {
}
