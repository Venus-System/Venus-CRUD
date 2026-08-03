package com.venus.crud.dto.request.user;

import jakarta.validation.constraints.NotNull;

public record UserProfileTagRequest(
        @NotNull Long userId,
        @NotNull Long profileTagId
) {
}
