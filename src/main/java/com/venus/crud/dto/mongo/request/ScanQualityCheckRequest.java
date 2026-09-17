package com.venus.crud.dto.mongo.request;

import com.venus.crud.entity.enums.AnalysisStatus;
import jakarta.validation.constraints.NotNull;

public record ScanQualityCheckRequest(
        @NotNull Double blurScore,
        @NotNull Double brightness,
        @NotNull Boolean backgroundOk,
        @NotNull AnalysisStatus status
) {
}
