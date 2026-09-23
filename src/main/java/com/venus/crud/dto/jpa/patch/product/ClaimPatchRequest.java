package com.venus.crud.dto.jpa.patch.product;

import com.venus.crud.entity.enums.ClaimType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ClaimPatchRequest(
        @Schema(description = "Nome da alegação.", example = "Vegano")
        String name,
        @Schema(description = "Descrição da alegação.", example = "Produto sem ingredientes de origem animal.")
        String description,
        @Schema(description = "Tipo da alegação.")
        ClaimType claimType
) {
}
