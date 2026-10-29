package com.venus.crud.dto.mongo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ScanIngredientRequest(
        @Schema(description = "Posição do ingrediente na lista do rótulo, começando em 1.", example = "12")
        @NotNull @Positive Integer position,
        @Schema(description = "Nome do ingrediente exatamente como o OCR leu.", example = "Coumarln")
        @NotBlank @Size(max = 300) String rawName
) {
}
