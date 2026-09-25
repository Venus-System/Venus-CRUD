package com.venus.crud.dto.mongo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ScanOcrFrontResponse(
        @Schema(description = "Texto completo que o OCR leu na frente.",
                example = "oBoticario CUIDE-SE BEM ROSA E ALGODAO body splash 200 ml")
        String fullText,
        @Schema(description = "Texto lido na frente, linha por linha, na ordem em que aparece.")
        List<String> lines,
        @Schema(description = "Campos que o app separou do texto da frente.")
        ScanFrontExtractedResponse extracted
) {
}
