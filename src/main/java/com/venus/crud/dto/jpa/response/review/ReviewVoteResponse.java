package com.venus.crud.dto.jpa.response.review;

import com.venus.crud.entity.enums.VoteType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record ReviewVoteResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador da avaliação.", example = "88")
        Long reviewId,
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Tipo do voto.")
        VoteType voteType,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt
) {
}
