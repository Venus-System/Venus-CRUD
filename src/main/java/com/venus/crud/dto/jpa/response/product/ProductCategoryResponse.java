package com.venus.crud.dto.jpa.response.product;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record ProductCategoryResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Nome da categoria de produto.", example = "Shampoo")
        String name,
        @Schema(description = "Descrição da categoria de produto.", example = "Produtos para limpeza dos fios.")
        String description,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
