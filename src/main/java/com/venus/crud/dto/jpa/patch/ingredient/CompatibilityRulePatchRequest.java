package com.venus.crud.dto.jpa.patch.ingredient;

import com.venus.crud.entity.enums.EffectType;
import com.venus.crud.entity.enums.EvidenceLevel;
import com.venus.crud.entity.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record CompatibilityRulePatchRequest(
        @Schema(description = "Identificador do efeito de ingrediente.", example = "61")
        Long ingredientEffectId,
        @Schema(description = "Identificador do modelo de score.", example = "1")
        Long scoringModelId,
        @Schema(description = "Tipo do efeito: desejado ou indesejado.")
        EffectType effectType,
        @Schema(description = "Quanto a regra soma ou subtrai do score.", example = "-5")
        Integer scoreDelta,
        @Schema(description = "Peso desta categoria no cálculo do score.", example = "0.30")
        BigDecimal weight,
        @Schema(description = "Prioridade de aplicação da regra; o menor valor é aplicado antes.", example = "10")
        Integer priority,
        @Schema(description = "Indica se a regra leva em conta a concentração do ingrediente.", example = "false")
        Boolean hasConcentrationFactor,
        @Schema(description = "Indica se a regra está habilitada no modelo.", example = "true")
        Boolean isEnabled,
        @Schema(description = "Força da evidência científica que sustenta a informação.")
        EvidenceLevel evidenceLevel,
        @Schema(description = "Motivo da regra, mostrado ao usuário quando ela dispara.",
                example = "Álcool resseca pele seca.")
        String reason,
        @Schema(description = "Origem do dado.")
        SourceType sourceType,
        @Schema(description = "Identificador do registro na fonte externa de onde ele veio.",
                example = "ANVISA-2024-0031")
        String sourceReference
) {
}
