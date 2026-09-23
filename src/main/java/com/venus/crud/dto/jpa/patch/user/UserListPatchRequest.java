package com.venus.crud.dto.jpa.patch.user;

import com.venus.crud.entity.enums.ListType;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserListPatchRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Nome da lista.", example = "Minha rotina noturna")
        String name,
        @Schema(description = "Tipo da lista.")
        ListType listType
) {
}
