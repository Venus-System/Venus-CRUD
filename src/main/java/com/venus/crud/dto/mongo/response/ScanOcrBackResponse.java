package com.venus.crud.dto.mongo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ScanOcrBackResponse(
        @Schema(description = "Texto completo que o OCR leu no verso.",
                example = "COMPOSICAO: ALCOHOL DENAT, AQUA, PARFUM, COUMARLN, GERANIOL")
        String fullText,
        @Schema(description = "Texto lido no verso, linha por linha, na ordem em que aparece.")
        List<String> lines,
        @Schema(description = "Campos que o app separou do texto do verso.")
        ScanBackExtractedResponse extracted
) {
}
