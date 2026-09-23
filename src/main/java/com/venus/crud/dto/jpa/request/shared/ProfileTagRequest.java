package com.venus.crud.dto.jpa.request.shared;

import com.venus.crud.entity.enums.ProfileTagCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProfileTagRequest(
        @Schema(description = "Nome da tag de perfil, como aparece na tela.", example = "Pele acneica")
        @NotBlank String name,
        @Schema(description = "Explicação da tag para o usuário.", example = "Pele com tendência a cravos e espinhas.")
        @NotNull String description,
        @Schema(description = "Identificador textual estável, em minúsculas e com hífens.", example = "pele-acneica")
        @NotBlank String slug,
        @Schema(description = "Categoria a que o registro pertence.")
        @NotNull ProfileTagCategory category
) {
}
