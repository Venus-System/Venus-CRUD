package com.venus.crud.dto.request.user;

import jakarta.validation.constraints.PositiveOrZero;

public record UserListItemPatchRequest(
        Long userListId,
        Long productId,
        @PositiveOrZero Integer positionOrder
) {
}
