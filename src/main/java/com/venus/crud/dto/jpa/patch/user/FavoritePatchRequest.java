package com.venus.crud.dto.jpa.patch.user;
import io.swagger.v3.oas.annotations.media.Schema;


public record FavoritePatchRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Identificador do produto.", example = "15")
        Long productId
) {
}
