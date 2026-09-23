package com.venus.crud.dto.jpa.response.ingredient;

import com.venus.crud.entity.enums.EffectCategory;
import com.venus.crud.entity.enums.EffectStrength;
import com.venus.crud.entity.enums.EvidenceLevel;
import com.venus.crud.entity.enums.ReviewStatus;
import com.venus.crud.entity.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record IngredientEffectResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador do ingrediente.", example = "100")
        Long ingredientId,
        @Schema(description = "Identificador da tag de perfil.", example = "9")
        Long profileTagId,
        @Schema(description = "Categoria do efeito do ingrediente.")
        EffectCategory effectCategory,
        @Schema(description = "Nome do efeito.", example = "Hidratação")
        String effectName,
        @Schema(description = "Descrição do efeito do ingrediente na pele ou no cabelo.",
                example = "Retém água na camada superficial da pele.")
        String effectDescription,
        @Schema(description = "Intensidade do efeito.")
        EffectStrength effectStrength,
        @Schema(description = "Força da evidência científica que sustenta a informação.")
        EvidenceLevel evidenceLevel,
        @Schema(description = "Situação de moderação da avaliação.")
        ReviewStatus reviewStatus,
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
