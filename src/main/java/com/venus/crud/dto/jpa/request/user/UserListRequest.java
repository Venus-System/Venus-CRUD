package com.venus.crud.dto.jpa.request.user;

import com.venus.crud.entity.enums.ListType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserListRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        @NotNull Long userId,
        @Schema(description = "Nome da lista.", example = "Minha rotina noturna")
        @NotBlank String name,
        @Schema(description = "Tipo da lista.")
        @NotNull ListType listType
) {
}
