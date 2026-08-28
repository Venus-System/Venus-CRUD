package com.venus.crud.dto.jpa.patch.scoring;

public record ScoringModelPatchRequest(
        String name,
        String version,
        String description,
        Boolean isActive
) {
}
