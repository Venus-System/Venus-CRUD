package com.venus.crud.dto.mongo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ScanFrontExtractedRequest(
        @Schema(description = "Marca como o OCR leu.", example = "oBoticario")
        @Size(max = 2000) String brand,
        @Schema(description = "Nome do produto como o OCR leu.", example = "CUIDE-SE BEM ROSA E ALGODAO")
        @Size(max = 2000) String productName,
        @Schema(description = "Apresentação do produto.", example = "body splash")
        @Size(max = 2000) String presentation,
        @Schema(description = "Capacidade informada na frente.", example = "200 ml")
        @Size(max = 2000) String capacity,
        @Schema(description = "Categoria que o app deduziu da frente.", example = "Corpo")
        @Size(max = 2000) String category,
        @Schema(description = "Alegações impressas na frente, como vegano ou sem parabenos.")
        @Size(max = 50) List<@Size(max = 200) String> claims
) {
}
