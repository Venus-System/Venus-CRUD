package com.venus.crud.dto.jpa.request.user;

import com.venus.crud.entity.enums.ListCoverKey;
import com.venus.crud.entity.enums.ListType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserListRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        @NotNull Long userId,
        @Schema(description = "Nome da lista.", example = "Minha rotina noturna")
        @NotBlank String name,
        @Schema(description = "Tipo da lista.")
        @NotNull ListType listType,
        @Schema(description = "Descrição da lista, com até 500 caracteres.", example = "Produtos que uso antes de dormir")
        @Size(max = 500) String description,
        @Schema(description = "Capa padrão da lista, usada quando ela não tem foto.")
        ListCoverKey coverKey
) {
}
