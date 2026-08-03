package com.venus.crud.dto.request.scan;

import com.venus.crud.entity.enums.ScanStatus;
import com.venus.crud.entity.enums.ScanStep;
import com.venus.crud.entity.enums.ScanType;
import jakarta.validation.constraints.NotNull;

public record ScanSessionRequest(
        @NotNull Long userId,
        @NotNull ScanType scanType,
        @NotNull ScanStatus status,
        @NotNull ScanStep currentStep,
        String deviceInfo
) {
}
