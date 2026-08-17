package com.venus.crud.dto.request.user;

public record FavoritePatchRequest(
        Long userId,
        Long productId
) {
}
