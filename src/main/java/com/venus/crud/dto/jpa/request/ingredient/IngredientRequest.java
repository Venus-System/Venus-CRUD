package com.venus.crud.dto.jpa.request.ingredient;

import com.venus.crud.entity.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IngredientRequest(
        @Schema(description = "Identificador da categoria do ingrediente.", example = "5")
        @NotNull Long ingredientCategoryId,
        @Schema(description = "Nome INCI do ingrediente, o padrão internacional que aparece no rótulo.",
                example = "Sodium Hyaluronate")
        @NotBlank String inciName,
        @Schema(description = "Nome comum do ingrediente, o que o público reconhece.", example = "Vitamina C")
        @NotBlank String commonName,
        @Schema(description = "Resumo da função do ingrediente na fórmula.", example = "Umectante")
        @NotBlank String functionSummary,
        @Schema(description = "Descrição do ingrediente.", example = "Ativo antioxidante usado em séruns faciais.")
        @NotBlank String description,
        @Schema(description = "Nível de biodegradabilidade do ingrediente; quanto maior, mais biodegradável.",
                example = "3")
        @NotNull Short biodegradabilityLevel,
        @Schema(description = "Nível de risco de irritação do ingrediente.", example = "3")
        @NotNull Short irritationRiskLevel,
        @Schema(description = "Potencial do ingrediente de obstruir os poros; quanto maior, maior o risco.",
                example = "2")
        @NotNull Short comedogenicityScore,
        @Schema(description = "Nível de risco ambiental do ingrediente.", example = "2")
        @NotNull Short environmentalRiskLevel,
        @Schema(description = "Resumo do que se sabe sobre a segurança do ingrediente.",
                example = "Seguro nas concentrações usuais em cosméticos.")
        @NotBlank String safetySummary,
        @Schema(description = "Confiança científica no dado; quanto maior, mais sustentado.", example = "85")
        @NotNull Short scientificConfidence,
        @Schema(description = "Origem do dado.")
        @NotNull SourceType sourceType,
        @Schema(description = "Identificador do registro na fonte externa de onde ele veio.",
                example = "ANVISA-2024-0031")
        String sourceReference
) {
}
