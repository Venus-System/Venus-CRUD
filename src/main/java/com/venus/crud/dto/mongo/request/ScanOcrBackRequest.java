package com.venus.crud.dto.mongo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ScanOcrBackRequest(
        @Schema(description = "Texto completo que o OCR leu no verso.",
                example = "COMPOSICAO: ALCOHOL DENAT, AQUA, PARFUM, COUMARLN, GERANIOL")
        @Size(max = 20000) String fullText,
        @Schema(description = "Texto lido no verso, linha por linha, na ordem em que aparece.")
        @Size(max = 500) List<@Size(max = 1000) String> lines,
        @Schema(description = "Campos que o app separou do texto do verso.")
        @Valid ScanBackExtractedRequest extracted
) {
}
