package com.venus.crud.dto.mongo.request;

import com.venus.crud.entity.enums.AnalysisStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ScanQualityCheckRequest(
        @Schema(description = "Medida de desfoque da foto; quanto maior, mais borrada.", example = "0.12")
        @NotNull Double blurScore,
        @Schema(description = "Medida de luminosidade da foto.", example = "0.68")
        @NotNull Double brightness,
        @Schema(description = "Indica se o fundo da foto passou na checagem de qualidade.", example = "true")
        @NotNull Boolean backgroundOk,
        @Schema(description = "Situação atual do registro.")
        @NotNull AnalysisStatus status
) {
}
