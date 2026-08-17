package com.venus.crud.dto.request.user;

import com.venus.crud.entity.enums.RoutineType;

public record RoutinePatchRequest(
        Long userId,
        RoutineType routineType,
        String name,
        String description
) {
}
