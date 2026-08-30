package com.venus.crud.dto.jpa.request.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UserListItemRequest(
        @NotNull Long userListId,
        @NotNull Long productId,
        @NotNull @PositiveOrZero Integer positionOrder
) {
}
