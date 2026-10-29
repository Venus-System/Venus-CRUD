package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.review.ReviewResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewFullResponse(
        @Schema(description = "Avaliação referenciada.")
        ReviewResponse review,
        @Schema(description = "Quantidade de votos de útil.", example = "12")
        long usefulVotes,
        @Schema(description = "Quantidade de votos de não útil.", example = "2")
        long notUsefulVotes
) {
}