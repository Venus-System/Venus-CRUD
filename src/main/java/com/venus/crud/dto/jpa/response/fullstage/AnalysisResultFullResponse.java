package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.scan.AnalysisResultResponse;
import com.venus.crud.dto.jpa.response.scan.PersonalizedScoreResponse;
import java.util.List;

public record AnalysisResultFullResponse(
        AnalysisResultResponse analysis,
        PersonalizedScoreResponse personalizedScore,
        List<RuleEvaluationDetailResponse> ruleEvaluations
) {
}