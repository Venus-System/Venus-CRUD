package com.venus.crud.dto.request.user;

public record UserProfileTagPatchRequest(
        Long userId,
        Long profileTagId
) {
}
