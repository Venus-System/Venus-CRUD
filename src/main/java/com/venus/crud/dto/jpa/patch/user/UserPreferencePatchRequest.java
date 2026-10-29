package com.venus.crud.dto.jpa.patch.user;
import io.swagger.v3.oas.annotations.media.Schema;


public record UserPreferencePatchRequest(
        @Schema(description = "Identificador do usuário.", example = "42")
        Long userId,
        @Schema(description = "Indica se o usuário prefere produtos não testados em animais.", example = "true")
        Boolean preferCrueltyFree,
        @Schema(description = "Indica se o usuário prefere produtos veganos.", example = "true")
        Boolean preferVegan,
        @Schema(description = "Indica se o usuário prefere produtos sustentáveis.", example = "true")
        Boolean preferSustainable,
        @Schema(description = "Indica se o usuário prefere produtos sem fragrância.", example = "true")
        Boolean preferFragranceFree,
        @Schema(description = "Indica se o usuário prefere produtos sem parabenos.", example = "false")
        Boolean preferParabenFree,
        @Schema(description = "Indica se o usuário prefere produtos sem sulfato.", example = "true")
        Boolean preferSulfateFree,
        @Schema(description = "Indica se o usuário prefere produtos sem silicone.", example = "false")
        Boolean preferSiliconeFree
) {
}
