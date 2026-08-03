package com.venus.crud.dto.response.user;

import java.time.OffsetDateTime;

public record UserProfileTagResponse(
        Long id,
        Long userId,
        Long profileTagId,
        OffsetDateTime createdAt
) {
}
