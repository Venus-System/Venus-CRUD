package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.scan.AnalysisResultResponse;
import com.venus.crud.dto.jpa.response.scan.PersonalizedScoreResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record AnalysisResultFullResponse(
        @Schema(description = "Resultado da análise ligada a este scan.")
        AnalysisResultResponse analysis,
        @Schema(description = "Nota do produto ajustada ao perfil do usuário.")
        PersonalizedScoreResponse personalizedScore,
        @Schema(description = "Regras de compatibilidade que foram disparadas na análise.")
        List<RuleEvaluationDetailResponse> ruleEvaluations
) {
}