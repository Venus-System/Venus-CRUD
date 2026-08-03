package com.venus.crud.dto.request.user;

import com.venus.crud.entity.enums.RoutineType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoutineRequest(
        @NotNull Long userId,
        @NotNull RoutineType routineType,
        @NotBlank String name,
        @NotNull String description
) {
}
