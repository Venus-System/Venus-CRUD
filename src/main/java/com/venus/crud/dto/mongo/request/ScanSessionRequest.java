package com.venus.crud.dto.mongo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ScanSessionRequest(
        @Schema(description = "Identificador do scan gerado pelo app; reenviar o mesmo valor não duplica o scan.",
                example = "8d3f2c1a-4b5e-4f6a-9c7d-1e2f3a4b5c6d")
        @NotNull UUID scanId,
        @Schema(description = "Identificador do usuário no Firebase.", example = "Xy12AbC34dEf56GhI78jKl90")
        @NotBlank @Size(max = 128) String firebaseUid,
        @Schema(description = "Aparelho que fez o scan.")
        @NotNull @Valid ScanDeviceRequest device,
        @Schema(description = "Data e hora de início.", example = "2026-09-22T14:30:00-03:00")
        @NotNull OffsetDateTime startedAt,
        @Schema(description = "Data e hora em que o processamento terminou.", example = "2026-09-22T14:30:00-03:00")
        @NotNull OffsetDateTime finishedAt,
        @Schema(description = "Checagem de qualidade da foto do scan.")
        @NotNull @Valid ScanQualityCheckRequest qualityCheck,
        @Schema(description = "Referência das fotos da frente e do verso já enviadas ao Cloudinary.")
        @Valid ScanImagesRequest images,
        @Schema(description = "Texto lido pelo OCR em cada lado da embalagem.")
        @NotNull @Valid ScanOcrRequest ocr,
        @Schema(description = "Ingredientes separados pelo app, na ordem do rótulo.")
        @Size(max = 200) List<@NotNull @Valid ScanIngredientRequest> ingredients
) {
}
