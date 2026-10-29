package com.venus.crud.dto.jpa.request.product;

import com.venus.crud.entity.enums.ClaimType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClaimRequest(
        @Schema(description = "Nome da alegação.", example = "Vegano")
        @NotBlank String name,
        @Schema(description = "Descrição da alegação.", example = "Produto sem ingredientes de origem animal.")
        @NotBlank String description,
        @Schema(description = "Tipo da alegação.")
        @NotNull ClaimType claimType
) {
}
