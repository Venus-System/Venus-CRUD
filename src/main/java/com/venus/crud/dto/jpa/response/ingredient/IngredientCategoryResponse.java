package com.venus.crud.dto.jpa.response.ingredient;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record IngredientCategoryResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador da categoria pai; vazio quando é categoria raiz.", example = "4")
        Long parentId,
        @Schema(description = "Nome da categoria pai.", example = "Tensoativo")
        String parentName,
        @Schema(description = "Nome da categoria de ingrediente.", example = "Tensoativo")
        String name,
        @Schema(description = "Descrição da categoria de ingrediente.",
                example = "Ingredientes responsáveis pela limpeza e pela espuma.")
        String description,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
