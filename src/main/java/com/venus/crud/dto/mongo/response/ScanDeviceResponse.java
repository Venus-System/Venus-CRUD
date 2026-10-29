package com.venus.crud.dto.mongo.response;
import io.swagger.v3.oas.annotations.media.Schema;


public record ScanDeviceResponse(
        @Schema(description = "Identificador do aparelho que originou o scan.", example = "9f8e7d6c-5b4a-3210")
        String deviceId,
        @Schema(description = "Plataforma do aparelho.", example = "Android")
        String platform,
        @Schema(description = "Versão do aplicativo que originou o scan.", example = "1.4.2")
        String appVersion,
        @Schema(description = "Modelo do aparelho.", example = "Moto G84")
        String model
) {
}
