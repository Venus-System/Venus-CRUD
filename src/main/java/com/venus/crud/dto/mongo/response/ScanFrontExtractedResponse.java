package com.venus.crud.dto.mongo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ScanFrontExtractedResponse(
        @Schema(description = "Marca como o OCR leu.", example = "oBoticario")
        String brand,
        @Schema(description = "Nome do produto como o OCR leu.", example = "CUIDE-SE BEM ROSA E ALGODAO")
        String productName,
        @Schema(description = "Apresentação do produto.", example = "body splash")
        String presentation,
        @Schema(description = "Capacidade informada na frente.", example = "200 ml")
        String capacity,
        @Schema(description = "Categoria que o app deduziu da frente.", example = "Corpo")
        String category,
        @Schema(description = "Alegações impressas na frente, como vegano ou sem parabenos.")
        List<String> claims
) {
}
