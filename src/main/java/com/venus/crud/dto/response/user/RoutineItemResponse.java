package com.venus.crud.dto.response.user;

import com.venus.crud.entity.enums.RoutineTime;
import java.time.OffsetDateTime;

public record RoutineItemResponse(
        Long id,
        Long routineId,
        Long productId,
        Integer stepOrder,
        RoutineTime usageTime,
        OffsetDateTime createdAt
) {
}
