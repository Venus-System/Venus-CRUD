package com.venus.crud.dto.jpa.patch.user;

public record UserProfileTagPatchRequest(
        Long userId,
        Long profileTagId
) {
}
