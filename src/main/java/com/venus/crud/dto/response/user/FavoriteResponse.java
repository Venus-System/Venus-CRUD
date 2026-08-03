package com.venus.crud.dto.response.user;

import java.time.OffsetDateTime;

public record FavoriteResponse(
        Long id,
        Long userId,
        Long productId,
        OffsetDateTime createdAt
) {
}
