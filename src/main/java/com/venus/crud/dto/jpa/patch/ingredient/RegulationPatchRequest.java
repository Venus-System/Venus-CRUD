package com.venus.crud.dto.jpa.patch.ingredient;

import com.venus.crud.entity.enums.RegulationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record RegulationPatchRequest(
        @Schema(description = "Título da norma.", example = "RDC 752/2022")
        String title,
        @Schema(description = "País, em código ISO de duas letras.", example = "BR")
        String country,
        @Schema(description = "Órgão regulador que publicou a norma.", example = "ANVISA")
        String agency,
        @Schema(description = "Endereço do documento oficial da norma.",
                example = "https://antigo.anvisa.gov.br/documentos/rdc-2024.pdf")
        String documentUrl,
        @Schema(description = "Situação atual do registro.")
        RegulationStatus status,
        @Schema(description = "Data em que a norma passa a valer.", example = "2026-01-01")
        LocalDate effectiveDate,
        @Schema(description = "Resumo da norma.", example = "Limita a concentração máxima em produtos sem enxágue.")
        String summary
) {
}
