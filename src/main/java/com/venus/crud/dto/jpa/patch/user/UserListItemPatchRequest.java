package com.venus.crud.dto.jpa.patch.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;

public record UserListItemPatchRequest(
        @Schema(description = "Identificador da lista do usuário.", example = "6")
        Long userListId,
        @Schema(description = "Identificador do produto.", example = "15")
        Long productId,
        @Schema(description = "Ordem de exibição.", example = "1")
        @PositiveOrZero Integer positionOrder
) {
}
