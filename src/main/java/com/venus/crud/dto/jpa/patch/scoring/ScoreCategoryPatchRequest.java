package com.venus.crud.dto.jpa.patch.scoring;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record ScoreCategoryPatchRequest(
        @Schema(description = "Nome da categoria de score.", example = "Segurança")
        String name,
        @Schema(description = "O que esta categoria avalia.", example = "Mede o risco dos ingredientes para a saúde.")
        String description,
        @Schema(description = "Peso padrão desta categoria no cálculo do score.", example = "0.25")
        BigDecimal defaultWeight
) {
}
