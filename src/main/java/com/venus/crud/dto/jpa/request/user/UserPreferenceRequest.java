package com.venus.crud.dto.jpa.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserPreferenceRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        @NotNull Long userId,
        @Schema(description = "Indica se o usuário prefere produtos não testados em animais.", example = "true")
        @NotNull Boolean preferCrueltyFree,
        @Schema(description = "Indica se o usuário prefere produtos veganos.", example = "true")
        @NotNull Boolean preferVegan,
        @Schema(description = "Indica se o usuário prefere produtos sustentáveis.", example = "true")
        @NotNull Boolean preferSustainable,
        @Schema(description = "Indica se o usuário prefere produtos sem fragrância.", example = "true")
        @NotNull Boolean preferFragranceFree,
        @Schema(description = "Indica se o usuário prefere produtos sem parabenos.", example = "false")
        @NotNull Boolean preferParabenFree,
        @Schema(description = "Indica se o usuário prefere produtos sem sulfato.", example = "true")
        @NotNull Boolean preferSulfateFree,
        @Schema(description = "Indica se o usuário prefere produtos sem silicone.", example = "false")
        @NotNull Boolean preferSiliconeFree
) {
}
