package com.venus.crud.dto.request.scoring;

public record ScoringModelPatchRequest(
        String name,
        String version,
        String description,
        Boolean isActive
) {
}
