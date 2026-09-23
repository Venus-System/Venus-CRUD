package com.venus.crud.dto.jpa.request.scoring;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ScoreCategoryRequest(
        @Schema(description = "Nome da categoria de score.", example = "Segurança")
        @NotBlank String name,
        @Schema(description = "O que esta categoria avalia.", example = "Mede o risco dos ingredientes para a saúde.")
        @NotBlank String description,
        @Schema(description = "Peso padrão desta categoria no cálculo do score.", example = "0.25")
        @NotNull BigDecimal defaultWeight
) {
}
