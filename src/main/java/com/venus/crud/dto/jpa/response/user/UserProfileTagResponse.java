package com.venus.crud.dto.jpa.response.user;

import java.time.OffsetDateTime;

public record UserProfileTagResponse(
        Long id,
        Long userId,
        Long profileTagId,
        OffsetDateTime createdAt
) {
}
