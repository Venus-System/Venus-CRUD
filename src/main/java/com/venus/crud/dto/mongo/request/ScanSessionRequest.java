package com.venus.crud.dto.mongo.request;

import com.venus.crud.entity.enums.AnalysisStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record ScanSessionRequest(
        @NotNull AnalysisStatus status,
        @NotNull @Valid ScanDeviceRequest device,
        @NotNull OffsetDateTime startedAt,
        @NotNull OffsetDateTime finishedAt,
        @NotNull @Valid ScanQualityCheckRequest qualityCheck,
        String fullOcrText
) {
}
