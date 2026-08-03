package com.venus.crud.dto.request.shared;

import com.venus.crud.entity.enums.ProfileTagCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProfileTagRequest(
        @NotBlank String name,
        @NotNull String description,
        @NotBlank String slug,
        @NotNull ProfileTagCategory category
) {
}
