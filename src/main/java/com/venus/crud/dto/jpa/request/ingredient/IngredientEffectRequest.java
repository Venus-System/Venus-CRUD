package com.venus.crud.dto.jpa.request.ingredient;

import com.venus.crud.entity.enums.EffectCategory;
import com.venus.crud.entity.enums.EffectStrength;
import com.venus.crud.entity.enums.EvidenceLevel;
import com.venus.crud.entity.enums.ReviewStatus;
import com.venus.crud.entity.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IngredientEffectRequest(
        @Schema(description = "Identificador do ingrediente.", example = "100")
        @NotNull Long ingredientId,
        @Schema(description = "Identificador da tag de perfil.", example = "9")
        @NotNull Long profileTagId,
        @Schema(description = "Categoria do efeito do ingrediente.")
        @NotNull EffectCategory effectCategory,
        @Schema(description = "Nome do efeito.", example = "Hidratação")
        @NotBlank String effectName,
        @Schema(description = "Descrição do efeito do ingrediente na pele ou no cabelo.",
                example = "Retém água na camada superficial da pele.")
        @NotBlank String effectDescription,
        @Schema(description = "Intensidade do efeito.")
        @NotNull EffectStrength effectStrength,
        @Schema(description = "Força da evidência científica que sustenta a informação.")
        @NotNull EvidenceLevel evidenceLevel,
        @Schema(description = "Situação de moderação da avaliação.")
        @NotNull ReviewStatus reviewStatus,
        @Schema(description = "Origem do dado.")
        @NotNull SourceType sourceType,
        @Schema(description = "Identificador do registro na fonte externa de onde ele veio.",
                example = "ANVISA-2024-0031")
        String sourceReference
) {
}
