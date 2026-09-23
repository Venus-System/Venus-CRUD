package com.venus.crud.dto.jpa.patch.product;

import com.venus.crud.entity.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProductLabelPatchRequest(
        @Schema(description = "Identificador da versão do produto.", example = "31")
        Long productVersionId,
        @Schema(description = "Texto do rótulo já normalizado para comparação.",
                example = "aqua sodium laureth sulfate cocamidopropyl betaine")
        String normalizedText,
        @Schema(description = "Idioma do texto, em código ISO.", example = "pt-BR")
        String language,
        @Schema(description = "Origem do dado.")
        SourceType sourceType,
        @Schema(description = "Identificador do registro na fonte externa de onde ele veio.",
                example = "ANVISA-2024-0031")
        String sourceReference
) {
}
