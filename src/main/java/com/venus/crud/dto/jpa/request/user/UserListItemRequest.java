package com.venus.crud.dto.jpa.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UserListItemRequest(
        @Schema(description = "Identificador da lista do usuário.", example = "6")
        @NotNull Long userListId,
        @Schema(description = "Identificador do produto.", example = "15")
        @NotNull Long productId,
        @Schema(description = "Ordem de exibição.", example = "1")
        @NotNull @PositiveOrZero Integer positionOrder
) {
}
