package com.venus.crud.dto.jpa.patch.review;

import com.venus.crud.entity.enums.VoteType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewVotePatchRequest(
        @Schema(description = "Identificador da avaliação.", example = "88")
        Long reviewId,
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Tipo do voto.")
        VoteType voteType
) {
}
