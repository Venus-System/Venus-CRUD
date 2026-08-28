package com.venus.crud.dto.jpa.request.scoring;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScoringModelRequest(
        @NotBlank String name,
        @NotBlank String version,
        @NotBlank String description,
        @NotNull Boolean isActive
) {
}
