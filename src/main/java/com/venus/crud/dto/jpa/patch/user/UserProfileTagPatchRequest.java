package com.venus.crud.dto.jpa.patch.user;
import io.swagger.v3.oas.annotations.media.Schema;


public record UserProfileTagPatchRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Identificador da tag de perfil.", example = "9")
        Long profileTagId
) {
}
