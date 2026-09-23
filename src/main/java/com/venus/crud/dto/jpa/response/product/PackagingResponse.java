package com.venus.crud.dto.jpa.response.product;

import com.venus.crud.entity.enums.PackagingFormat;
import com.venus.crud.entity.enums.PackagingMaterial;
import com.venus.crud.entity.enums.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PackagingResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador da versão do produto.", example = "31")
        Long productVersionId,
        @Schema(description = "Material da embalagem.")
        PackagingMaterial material,
        @Schema(description = "Detalhe do material, quando o tipo não basta.", example = "PET reciclado")
        String materialDetail,
        @Schema(description = "Formato da embalagem.")
        PackagingFormat packagingFormat,
        @Schema(description = "Indica se a embalagem é reciclável.", example = "true")
        Boolean isRecyclable,
        @Schema(description = "Indica se a embalagem é refilável.", example = "false")
        Boolean isRefillable,
        @Schema(description = "Indica se o material é biodegradável.", example = "true")
        Boolean isBiodegradable,
        @Schema(description = "Percentual de material reciclado na embalagem.", example = "30.0")
        BigDecimal recycledContentPercentage,
        @Schema(description = "Grau de confiança no dado; quanto maior, mais confiável.", example = "90")
        Short confidenceScore,
        @Schema(description = "Origem do dado.")
        SourceType sourceType,
        @Schema(description = "Identificador do registro na fonte externa de onde ele veio.",
                example = "ANVISA-2024-0031")
        String sourceReference,
        @Schema(description = "Indica se houve verificação manual.", example = "false")
        Boolean wasManualVerified,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
