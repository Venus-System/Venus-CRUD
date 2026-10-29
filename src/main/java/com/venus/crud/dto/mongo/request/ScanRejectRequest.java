package com.venus.crud.dto.mongo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ScanRejectRequest(
        @Schema(description = "Motivo da recusa.", example = "Foto do verso ilegível")
        @NotBlank @Size(max = 1000) String reason
) {
}
