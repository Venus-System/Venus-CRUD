package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.scoring.ProductScoreResponse;
import com.venus.crud.dto.jpa.response.scoring.ScoreCategoryResponse;
import com.venus.crud.dto.jpa.response.scoring.ScoringModelResponse;
import java.util.List;

public record ProductScoreFullResponse(
        ProductScoreResponse score,
        ScoringModelResponse scoringModel,
        List<ScoreCategoryResponse> categories
) {
}