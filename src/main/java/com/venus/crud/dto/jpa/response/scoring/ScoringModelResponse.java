package com.venus.crud.dto.jpa.response.scoring;

import java.time.OffsetDateTime;

public record ScoringModelResponse(
        Long id,
        String name,
        String version,
        String description,
        Boolean isActive,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
