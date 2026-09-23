package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.scoring.ProductScoreResponse;
import com.venus.crud.dto.jpa.response.scoring.ScoringModelCategoryResponse;
import com.venus.crud.dto.jpa.response.scoring.ScoringModelResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ProductScoreFullResponse(
        @Schema(description = "Score do produto.")
        ProductScoreResponse score,
        @Schema(description = "Modelo de score usado no cálculo.")
        ScoringModelResponse scoringModel,
        @Schema(description = "Categorias de score que compõem o modelo, com o peso de cada uma.")
        List<ScoringModelCategoryResponse> categories
) {
}