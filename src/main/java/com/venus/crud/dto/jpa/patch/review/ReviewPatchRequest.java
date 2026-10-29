package com.venus.crud.dto.jpa.patch.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record ReviewPatchRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Identificador da versão do produto.", example = "31")
        Long productVersionId,
        @Schema(description = "Nota dada pelo usuário.", example = "4.5")
        @DecimalMin("0.0") @DecimalMax("5.0") BigDecimal rating,
        @Schema(description = "Título da avaliação.", example = "Ótimo para pele seca")
        String title,
        @Schema(description = "Comentário do usuário sobre o produto.",
                example = "Ressecou minha pele depois de duas semanas.")
        String comment,
        @Schema(description = "Indica se o uso do ingrediente foi verificado.", example = "true")
        Boolean verifiedUse
) {
}
