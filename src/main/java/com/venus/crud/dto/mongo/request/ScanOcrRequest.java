package com.venus.crud.dto.mongo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

public record ScanOcrRequest(
        @Schema(description = "Texto lido na frente da embalagem.")
        @Valid ScanOcrFrontRequest front,
        @Schema(description = "Texto lido no verso da embalagem.")
        @Valid ScanOcrBackRequest back
) {
}
