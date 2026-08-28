package com.venus.crud.dto.jpa.request.user;

import jakarta.validation.constraints.NotNull;

public record UserProfileTagRequest(
        @NotNull Long userId,
        @NotNull Long profileTagId
) {
}
