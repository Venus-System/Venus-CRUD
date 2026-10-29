package com.venus.crud.dto.mongo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ScanDeviceRequest(
        @Schema(description = "Identificador do aparelho que originou o scan.", example = "9f8e7d6c-5b4a-3210")
        @NotBlank String deviceId,
        @Schema(description = "Plataforma do aparelho.", example = "Android")
        @NotBlank String platform,
        @Schema(description = "Versão do aplicativo que originou o scan.", example = "1.4.2")
        @NotBlank String appVersion,
        @Schema(description = "Modelo do aparelho.", example = "Moto G84")
        @NotBlank String model
) {
}
