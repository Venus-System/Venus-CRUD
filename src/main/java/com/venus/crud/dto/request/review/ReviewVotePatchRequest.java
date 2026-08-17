package com.venus.crud.dto.request.review;

import com.venus.crud.entity.enums.VoteType;

public record ReviewVotePatchRequest(
        Long reviewId,
        Long userId,
        VoteType voteType
) {
}
