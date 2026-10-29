package com.venus.crud.dto.mongo.response.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.AnalysisResultFullResponse;
import com.venus.crud.dto.jpa.response.fullstage.ProductFullResponse;
import com.venus.crud.dto.jpa.response.product.ProductVersionResponse;
import com.venus.crud.dto.mongo.response.ScanSessionResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public record ScanFullResponse(
        @Schema(description = "Sessão de scan.")
        ScanSessionResponse scan,
        @Schema(description = "Resultado da análise ligada a este scan.")
        AnalysisResultFullResponse analysis,
        @Schema(description = "Produto referenciado.")
        ProductFullResponse product,
        @Schema(description = "Versão do produto que foi analisada.")
        ProductVersionResponse analyzedVersion,
        @Schema(description = "Indica se o produto mudou depois que o scan foi feito.", example = "false")
        boolean productChangedSinceScan
) {
}
