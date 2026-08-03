package com.venus.crud.dto.request.user;

import jakarta.validation.constraints.NotNull;

public record FavoriteRequest(
        @NotNull Long userId,
        @NotNull Long productId
) {
}
