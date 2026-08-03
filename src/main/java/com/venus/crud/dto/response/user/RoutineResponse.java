package com.venus.crud.dto.response.user;

import com.venus.crud.entity.enums.RoutineType;
import java.time.OffsetDateTime;

public record RoutineResponse(
        Long id,
        Long userId,
        RoutineType routineType,
        String name,
        String description,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
