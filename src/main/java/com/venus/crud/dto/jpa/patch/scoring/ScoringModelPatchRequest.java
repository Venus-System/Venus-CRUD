package com.venus.crud.dto.jpa.patch.scoring;
import io.swagger.v3.oas.annotations.media.Schema;


public record ScoringModelPatchRequest(
        @Schema(description = "Nome do modelo de score.", example = "Modelo padrão Venus")
        String name,
        @Schema(description = "Versão do modelo.", example = "1.0.0")
        String version,
        @Schema(description = "Descrição do modelo de score.",
                example = "Modelo usado na análise padrão do aplicativo.")
        String description,
        @Schema(description = "Indica se o registro está ativo.", example = "true")
        Boolean isActive
) {
}
