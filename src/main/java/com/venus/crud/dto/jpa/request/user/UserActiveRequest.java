package com.venus.crud.dto.jpa.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserActiveRequest(
        @Schema(description = "Se o usuário fica ativo (true) ou inativo (false).", example = "false")
        @NotNull Boolean active
) {
}
