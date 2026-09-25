package com.venus.crud.dto.mongo.response;

import com.venus.crud.entity.enums.ScanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;

public record ScanSessionResponse(
        @Schema(description = "Identificador do registro.", example = "665f1b2c9a4e3b0012ab34cd")
        String id,
        @Schema(description = "Identificador do scan gerado pelo app.", example = "8d3f2c1a-4b5e-4f6a-9c7d-1e2f3a4b5c6d")
        String scanId,
        @Schema(description = "Situação do scan no ciclo de revisão.")
        ScanStatus status,
        @Schema(description = "Usuário que enviou o scan.")
        ScanSourceResponse source,
        @Schema(description = "Aparelho que fez o scan.")
        ScanDeviceResponse device,
        @Schema(description = "Data e hora de início.", example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime startedAt,
        @Schema(description = "Data e hora em que o processamento terminou.", example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime finishedAt,
        @Schema(description = "Checagem de qualidade da foto do scan.")
        ScanQualityCheckResponse qualityCheck,
        @Schema(description = "Fotos da frente e do verso no Cloudinary.")
        ScanImagesResponse images,
        @Schema(description = "Texto lido pelo OCR em cada lado da embalagem.")
        ScanOcrResponse ocr,
        @Schema(description = "Ingredientes do rótulo com o resultado da busca no catálogo.")
        List<ScanIngredientResponse> ingredients,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
