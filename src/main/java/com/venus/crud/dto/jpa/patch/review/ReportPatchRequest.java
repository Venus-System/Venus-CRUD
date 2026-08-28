package com.venus.crud.dto.jpa.patch.review;

import com.venus.crud.entity.enums.ReportStatus;
import com.venus.crud.entity.enums.ReportTargetType;
import java.time.OffsetDateTime;

public record ReportPatchRequest(
        Long userId,
        Long adminUserId,
        ReportTargetType targetType,
        Long targetId,
        String reason,
        ReportStatus status,
        OffsetDateTime handledAt
) {
}
