package com.venus.crud.dto.jpa.request.product;

import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.enums.VersionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ProductVersionRequest(
        @Schema(description = "Identificador do produto.", example = "15")
        @NotNull Long productId,
        @Schema(description = "Nome desta versão do produto.", example = "Fórmula 2026")
        @NotBlank String versionName,
        @Schema(description = "Nome exibido na interface.", example = "Ácido Hialurônico")
        @NotBlank String displayName,
        @Schema(description = "Situação atual do registro.")
        @NotNull VersionStatus status,
        @Schema(description = "Indica se esta é a versão vigente do produto.", example = "true")
        @NotNull Boolean isCurrent,
        @Schema(description = "Assinatura que identifica a formulação; muda quando a fórmula muda.",
                example = "a3f9c1d84b2e")
        @NotBlank String formulaSignature,
        @Schema(description = "Origem que detectou a informação.")
        @NotNull SourceType detectedBy,
        @Schema(description = "Início da vigência.", example = "2026-01-01")
        @NotNull LocalDate effectiveFrom,
        @Schema(description = "Fim da vigência; vazio enquanto estiver vigente.", example = "2027-12-31")
        LocalDate effectiveTo
) {
}
