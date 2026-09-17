package com.venus.crud.dto.mongo.response;

public record ScanDeviceResponse(
        String deviceId,
        String platform,
        String appVersion,
        String model
) {
}
