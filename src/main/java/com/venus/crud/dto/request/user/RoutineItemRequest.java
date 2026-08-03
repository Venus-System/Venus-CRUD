package com.venus.crud.dto.request.user;

import com.venus.crud.entity.enums.RoutineTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record RoutineItemRequest(
        @NotNull Long routineId,
        @NotNull Long productId,
        @NotNull @PositiveOrZero Integer stepOrder,
        @NotNull RoutineTime usageTime
) {
}
