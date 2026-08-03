package com.venus.crud.dto.response.scan;

import com.venus.crud.entity.enums.ScanStatus;
import com.venus.crud.entity.enums.ScanStep;
import com.venus.crud.entity.enums.ScanType;
import java.time.OffsetDateTime;

public record ScanSessionResponse(
        Long id,
        Long userId,
        ScanType scanType,
        ScanStatus status,
        ScanStep currentStep,
        String deviceInfo,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
