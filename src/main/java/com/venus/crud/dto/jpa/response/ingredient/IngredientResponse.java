package com.venus.crud.dto.jpa.response.ingredient;

import com.venus.crud.entity.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record IngredientResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador da categoria do ingrediente.", example = "5")
        Long ingredientCategoryId,
        @Schema(description = "Nome INCI do ingrediente, o padrão internacional que aparece no rótulo.",
                example = "Sodium Hyaluronate")
        String inciName,
        @Schema(description = "Nome comum do ingrediente, o que o público reconhece.", example = "Vitamina C")
        String commonName,
        @Schema(description = "Resumo da função do ingrediente na fórmula.", example = "Umectante")
        String functionSummary,
        @Schema(description = "Descrição do ingrediente.", example = "Ativo antioxidante usado em séruns faciais.")
        String description,
        @Schema(description = "Nível de biodegradabilidade do ingrediente; quanto maior, mais biodegradável.",
                example = "3")
        Short biodegradabilityLevel,
        @Schema(description = "Nível de risco de irritação do ingrediente.", example = "3")
        Short irritationRiskLevel,
        @Schema(description = "Potencial do ingrediente de obstruir os poros; quanto maior, maior o risco.",
                example = "2")
        Short comedogenicityScore,
        @Schema(description = "Nível de risco ambiental do ingrediente.", example = "2")
        Short environmentalRiskLevel,
        @Schema(description = "Resumo do que se sabe sobre a segurança do ingrediente.",
                example = "Seguro nas concentrações usuais em cosméticos.")
        String safetySummary,
        @Schema(description = "Confiança científica no dado; quanto maior, mais sustentado.", example = "85")
        Short scientificConfidence,
        @Schema(description = "Origem do dado.")
        SourceType sourceType,
        @Schema(description = "Identificador do registro na fonte externa de onde ele veio.",
                example = "ANVISA-2024-0031")
        String sourceReference,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
