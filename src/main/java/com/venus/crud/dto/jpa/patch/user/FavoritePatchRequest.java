package com.venus.crud.dto.jpa.patch.user;

public record FavoritePatchRequest(
        Long userId,
        Long productId
) {
}
