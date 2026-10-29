package com.venus.crud.dto.mongo.response;

import com.venus.crud.entity.enums.AnalysisStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record ScanQualityCheckResponse(
        @Schema(description = "Medida de desfoque da foto; quanto maior, mais borrada.", example = "0.12")
        Double blurScore,
        @Schema(description = "Medida de luminosidade da foto.", example = "0.68")
        Double brightness,
        @Schema(description = "Indica se o fundo da foto passou na checagem de qualidade.", example = "true")
        Boolean backgroundOk,
        @Schema(description = "Situação atual do registro.")
        AnalysisStatus status
) {
}
