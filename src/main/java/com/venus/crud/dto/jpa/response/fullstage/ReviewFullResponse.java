package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.review.ReviewResponse;

public record ReviewFullResponse(
        ReviewResponse review,
        long usefulVotes,
        long notUsefulVotes
) {
}