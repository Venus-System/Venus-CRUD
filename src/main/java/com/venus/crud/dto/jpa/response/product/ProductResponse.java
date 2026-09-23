package com.venus.crud.dto.jpa.response.product;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record ProductResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador da marca.", example = "3")
        Long brandId,
        @Schema(description = "Identificador da categoria do produto.", example = "2")
        Long productCategoryId,
        @Schema(description = "Nome do produto.", example = "Shampoo Hidratante 400ml")
        String name,
        @Schema(description = "Descrição do produto.", example = "Shampoo para cabelos secos, sem sulfato.")
        String description,
        @Schema(description = "Identificador textual estável, em minúsculas e com hífens.", example = "pele-acneica")
        String slug,
        @Schema(description = "Indica se o registro está ativo.", example = "true")
        Boolean isActive,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
