package com.venus.crud.dto.jpa.response.product;

import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.enums.VersionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record ProductVersionResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador do produto.", example = "15")
        Long productId,
        @Schema(description = "Nome desta versão do produto.", example = "Fórmula 2026")
        String versionName,
        @Schema(description = "Nome exibido na interface.", example = "Ácido Hialurônico")
        String displayName,
        @Schema(description = "Situação atual do registro.")
        VersionStatus status,
        @Schema(description = "Indica se esta é a versão vigente do produto.", example = "true")
        Boolean isCurrent,
        @Schema(description = "Assinatura que identifica a formulação; muda quando a fórmula muda.",
                example = "a3f9c1d84b2e")
        String formulaSignature,
        @Schema(description = "Origem que detectou a informação.")
        SourceType detectedBy,
        @Schema(description = "Início da vigência.", example = "2026-01-01")
        LocalDate effectiveFrom,
        @Schema(description = "Fim da vigência; vazio enquanto estiver vigente.", example = "2027-12-31")
        LocalDate effectiveTo,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
