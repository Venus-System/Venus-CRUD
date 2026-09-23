package com.venus.crud.dto.jpa.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserProfileTagRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        @NotNull Long userId,
        @Schema(description = "Identificador da tag de perfil.", example = "9")
        @NotNull Long profileTagId
) {
}
