package com.venus.crud.dto.request.user;

import jakarta.validation.constraints.NotNull;

public record UserPreferenceRequest(
        @NotNull Long userId,
        @NotNull Boolean preferCrueltyFree,
        @NotNull Boolean preferVegan,
        @NotNull Boolean preferSustainable,
        @NotNull Boolean preferFragranceFree,
        @NotNull Boolean preferParabenFree,
        @NotNull Boolean preferSulfateFree,
        @NotNull Boolean preferSiliconeFree
) {
}
