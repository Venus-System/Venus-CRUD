package com.venus.crud.dto.response.shared;

import com.venus.crud.entity.enums.ProfileTagCategory;
import java.time.OffsetDateTime;

public record ProfileTagResponse(
        Long id,
        String name,
        String description,
        String slug,
        ProfileTagCategory category,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
