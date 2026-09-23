package com.venus.crud.dto.jpa.request.scoring;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScoringModelRequest(
        @Schema(description = "Nome do modelo de score.", example = "Modelo padrão Venus")
        @NotBlank String name,
        @Schema(description = "Versão do modelo.", example = "1.0.0")
        @NotBlank String version,
        @Schema(description = "Descrição do modelo de score.",
                example = "Modelo usado na análise padrão do aplicativo.")
        @NotBlank String description,
        @Schema(description = "Indica se o registro está ativo.", example = "true")
        @NotNull Boolean isActive
) {
}
