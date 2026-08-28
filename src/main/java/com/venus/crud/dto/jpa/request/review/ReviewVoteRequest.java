package com.venus.crud.dto.jpa.request.review;

import com.venus.crud.entity.enums.VoteType;
import jakarta.validation.constraints.NotNull;

public record ReviewVoteRequest(
        @NotNull Long reviewId,
        @NotNull Long userId,
        @NotNull VoteType voteType
) {
}
