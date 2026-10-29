package com.venus.crud.dto.mongo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ScanOcrFrontRequest(
        @Schema(description = "Texto completo que o OCR leu na frente.",
                example = "oBoticario CUIDE-SE BEM ROSA E ALGODAO body splash 200 ml")
        @Size(max = 20000) String fullText,
        @Schema(description = "Texto lido na frente, linha por linha, na ordem em que aparece.")
        @Size(max = 500) List<@Size(max = 1000) String> lines,
        @Schema(description = "Campos que o app separou do texto da frente.")
        @Valid ScanFrontExtractedRequest extracted
) {
}
