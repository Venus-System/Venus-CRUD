package com.venus.crud.dto.response.user;

import java.time.OffsetDateTime;

public record UserListItemResponse(
        Long id,
        Long userListId,
        Long productId,
        Integer positionOrder,
        OffsetDateTime createdAt
) {
}
