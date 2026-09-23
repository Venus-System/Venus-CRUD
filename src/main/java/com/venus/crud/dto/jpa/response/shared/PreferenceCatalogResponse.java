package com.venus.crud.dto.jpa.response.shared;

import com.venus.crud.entity.enums.ProfileTagCategory;
import io.swagger.v3.oas.annotations.media.Schema;

public record PreferenceCatalogResponse(
        @Schema(description = "Identificador do registro.", example = "42")
        Long id,
        @Schema(description = "Identificador textual estável, em minúsculas e com hífens.", example = "pele-acneica")
        String slug,
        @Schema(description = "Nome da preferência, como aparece na tela.", example = "Sem fragrância")
        String name,
        @Schema(description = "Explicação da preferência para o usuário.",
                example = "Evita produtos com fragrância adicionada.")
        String description,
        @Schema(description = "Categoria a que o registro pertence.")
        ProfileTagCategory category,
        @Schema(description = "Campo de user_preferences onde gravar a marcação; vazio quando a marcação vira linha em user_profile_tags.",
                example = "preferVegan")
        String userPreferenceField
) {
}
