package com.venus.crud.dto.mongo.request;

import jakarta.validation.constraints.NotBlank;

public record ScanDeviceRequest(
        @NotBlank String deviceId,
        @NotBlank String platform,
        @NotBlank String appVersion,
        @NotBlank String model
) {
}
