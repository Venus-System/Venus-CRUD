package com.venus.crud.dto.jpa.patch.review;

import com.venus.crud.entity.enums.VoteType;

public record ReviewVotePatchRequest(
        Long reviewId,
        Long userId,
        VoteType voteType
) {
}
