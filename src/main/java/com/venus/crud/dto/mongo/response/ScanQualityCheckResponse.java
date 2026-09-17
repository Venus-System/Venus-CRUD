package com.venus.crud.dto.mongo.response;

import com.venus.crud.entity.enums.AnalysisStatus;

public record ScanQualityCheckResponse(
        Double blurScore,
        Double brightness,
        Boolean backgroundOk,
        AnalysisStatus status
) {
}
