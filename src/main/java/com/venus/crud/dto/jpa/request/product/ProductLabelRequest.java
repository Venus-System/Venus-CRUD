package com.venus.crud.dto.jpa.request.product;

import com.venus.crud.entity.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductLabelRequest(
        @Schema(description = "Identificador da versão do produto.", example = "31")
        @NotNull Long productVersionId,
        @Schema(description = "Texto do rótulo já normalizado para comparação.",
                example = "aqua sodium laureth sulfate cocamidopropyl betaine")
        @NotBlank String normalizedText,
        @Schema(description = "Idioma do texto, em código ISO.", example = "pt-BR")
        @NotBlank String language,
        @Schema(description = "Origem do dado.")
        @NotNull SourceType sourceType,
        @Schema(description = "Identificador do registro na fonte externa de onde ele veio.",
                example = "ANVISA-2024-0031")
        String sourceReference
) {
}
