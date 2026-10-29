package com.venus.crud.dto.jpa.patch.user;

import com.venus.crud.entity.enums.ListCoverKey;
import com.venus.crud.entity.enums.ListType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

public record UserListPatchRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Nome da lista.", example = "Minha rotina noturna")
        String name,
        @Schema(description = "Tipo da lista.")
        ListType listType,
        @Schema(description = "Descrição da lista, com até 500 caracteres. Enviar null apaga o valor; omitir o campo mantém o atual.",
                example = "Produtos que uso antes de dormir", implementation = String.class, nullable = true)
        @Size(max = 500) JsonNullable<String> description,
        @Schema(description = "Capa padrão da lista, usada quando ela não tem foto. Enviar null apaga o valor; omitir o campo mantém o atual.",
                implementation = ListCoverKey.class, nullable = true)
        JsonNullable<ListCoverKey> coverKey
) {
}
