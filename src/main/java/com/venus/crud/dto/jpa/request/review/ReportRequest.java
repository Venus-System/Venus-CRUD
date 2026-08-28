package com.venus.crud.dto.jpa.request.review;

import com.venus.crud.entity.enums.ReportStatus;
import com.venus.crud.entity.enums.ReportTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record ReportRequest(
        @NotNull Long userId,
        Long adminUserId,
        @NotNull ReportTargetType targetType,
        @NotNull Long targetId,
        @NotBlank String reason,
        @NotNull ReportStatus status,
        OffsetDateTime handledAt
) {
}
