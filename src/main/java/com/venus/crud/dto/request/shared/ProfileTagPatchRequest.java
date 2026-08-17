package com.venus.crud.dto.request.shared;

import com.venus.crud.entity.enums.ProfileTagCategory;

public record ProfileTagPatchRequest(
        String name,
        String description,
        String slug,
        ProfileTagCategory category
) {
}
