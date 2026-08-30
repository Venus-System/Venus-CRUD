package com.venus.crud.dto.jpa.response.review;

import com.venus.crud.entity.enums.VoteType;
import java.time.OffsetDateTime;

public record ReviewVoteResponse(
        Long id,
        Long reviewId,
        Long userId,
        VoteType voteType,
        OffsetDateTime createdAt
) {
}
