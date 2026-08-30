package com.venus.crud.dto.jpa.response.review;

import com.venus.crud.entity.enums.ReportStatus;
import com.venus.crud.entity.enums.ReportTargetType;
import java.time.OffsetDateTime;

public record ReportResponse(
        Long id,
        Long userId,
        Long adminUserId,
        ReportTargetType targetType,
        Long targetId,
        String reason,
        ReportStatus status,
        OffsetDateTime handledAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
