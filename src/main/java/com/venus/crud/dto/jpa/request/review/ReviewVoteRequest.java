package com.venus.crud.dto.jpa.request.review;

import com.venus.crud.entity.enums.VoteType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ReviewVoteRequest(
        @Schema(description = "Identificador da avaliação.", example = "88")
        @NotNull Long reviewId,
        @Schema(description = "Identificador do usuário.", example = "42")
        @NotNull Long userId,
        @Schema(description = "Tipo do voto.")
        @NotNull VoteType voteType
) {
}
