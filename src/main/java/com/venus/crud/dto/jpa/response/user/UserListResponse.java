package com.venus.crud.dto.jpa.response.user;

import com.venus.crud.entity.enums.ListCoverKey;
import com.venus.crud.entity.enums.ListType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

public record UserListResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Nome da lista.", example = "Minha rotina noturna")
        String name,
        @Schema(description = "Tipo da lista.")
        ListType listType,
        @Schema(description = "Descrição da lista. Nulo quando não foi informada.", example = "Produtos que uso antes de dormir")
        String description,
        @Schema(description = "Capa padrão da lista, usada quando ela não tem foto. Nulo quando não foi escolhida.")
        ListCoverKey coverKey,
        @Schema(description = "Endereço da foto de capa. Nulo quando a lista não tem foto.",
                example = "https://res.cloudinary.com/venus/image/upload/v1/user-lists/42/cover/8f3c1d.jpg")
        String coverUrl,
        @Schema(description = "Data e hora de criação do registro. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime createdAt,
        @Schema(description = "Data e hora da última atualização. Gerado pelo banco.",
                example = "2026-09-22T14:30:00-03:00")
        OffsetDateTime updatedAt
) {
}
