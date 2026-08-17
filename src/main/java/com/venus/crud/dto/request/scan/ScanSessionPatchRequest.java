package com.venus.crud.dto.request.scan;

import com.venus.crud.entity.enums.ScanStatus;
import com.venus.crud.entity.enums.ScanStep;
import com.venus.crud.entity.enums.ScanType;

public record ScanSessionPatchRequest(
        Long userId,
        ScanType scanType,
        ScanStatus status,
        ScanStep currentStep,
        String deviceInfo
) {
}
