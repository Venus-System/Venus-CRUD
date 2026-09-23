package com.venus.crud.dto.jpa.request.product;

import com.venus.crud.entity.enums.PackagingFormat;
import com.venus.crud.entity.enums.PackagingMaterial;
import com.venus.crud.entity.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record PackagingRequest(
        @Schema(description = "Identificador da versão do produto.", example = "31")
        @NotNull Long productVersionId,
        @Schema(description = "Material da embalagem.")
        @NotNull PackagingMaterial material,
        @Schema(description = "Detalhe do material, quando o tipo não basta.", example = "PET reciclado")
        @NotBlank String materialDetail,
        @Schema(description = "Formato da embalagem.")
        @NotNull PackagingFormat packagingFormat,
        @Schema(description = "Indica se a embalagem é reciclável.", example = "true")
        @NotNull Boolean isRecyclable,
        @Schema(description = "Indica se a embalagem é refilável.", example = "false")
        @NotNull Boolean isRefillable,
        @Schema(description = "Indica se o material é biodegradável.", example = "true")
        @NotNull Boolean isBiodegradable,
        @Schema(description = "Percentual de material reciclado na embalagem.", example = "30.0")
        @PositiveOrZero BigDecimal recycledContentPercentage,
        @Schema(description = "Grau de confiança no dado; quanto maior, mais confiável.", example = "90")
        @NotNull @PositiveOrZero Short confidenceScore,
        @Schema(description = "Origem do dado.")
        @NotNull SourceType sourceType,
        @Schema(description = "Identificador do registro na fonte externa de onde ele veio.",
                example = "ANVISA-2024-0031")
        String sourceReference,
        @Schema(description = "Indica se houve verificação manual.", example = "false")
        @NotNull Boolean wasManualVerified
) {
}
