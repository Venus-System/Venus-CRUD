package com.venus.crud.dto.request.user;

import com.venus.crud.entity.enums.RoutineTime;
import jakarta.validation.constraints.PositiveOrZero;

public record RoutineItemPatchRequest(
        Long routineId,
        Long productId,
        @PositiveOrZero Integer stepOrder,
        RoutineTime usageTime
) {
}
