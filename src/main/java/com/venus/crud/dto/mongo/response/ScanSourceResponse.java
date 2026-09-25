package com.venus.crud.dto.mongo.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ScanSourceResponse(
        @Schema(description = "Identificador do usuário que enviou o scan.", example = "42")
        Long userId,
        @Schema(description = "Identificador do usuário no Firebase.", example = "Xy12AbC34dEf56GhI78jKl90")
        String firebaseUid
) {
}
