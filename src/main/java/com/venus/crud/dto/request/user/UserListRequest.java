package com.venus.crud.dto.request.user;

import com.venus.crud.entity.enums.ListType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserListRequest(
        @NotNull Long userId,
        @NotBlank String name,
        @NotNull ListType listType
) {
}
