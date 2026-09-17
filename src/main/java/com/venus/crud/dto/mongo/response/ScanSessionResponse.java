package com.venus.crud.dto.mongo.response;

import com.venus.crud.entity.enums.AnalysisStatus;
import java.time.OffsetDateTime;

public record ScanSessionResponse(
        String id,
        AnalysisStatus status,
        ScanDeviceResponse device,
        OffsetDateTime startedAt,
        OffsetDateTime finishedAt,
        ScanQualityCheckResponse qualityCheck,
        String fullOcrText,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
