package com.venus.crud.dto.jpa.patch.shared;

import com.venus.crud.entity.enums.ProfileTagCategory;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProfileTagPatchRequest(
        @Schema(description = "Nome da tag de perfil, como aparece na tela.", example = "Pele acneica")
        String name,
        @Schema(description = "Explicação da tag para o usuário.", example = "Pele com tendência a cravos e espinhas.")
        String description,
        @Schema(description = "Identificador textual estável, em minúsculas e com hífens.", example = "pele-acneica")
        String slug,
        @Schema(description = "Categoria a que o registro pertence.")
        ProfileTagCategory category
) {
}
