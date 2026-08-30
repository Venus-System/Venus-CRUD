package com.venus.crud.dto.jpa.patch.user;

public record UserPreferencePatchRequest(
        Long userId,
        Boolean preferCrueltyFree,
        Boolean preferVegan,
        Boolean preferSustainable,
        Boolean preferFragranceFree,
        Boolean preferParabenFree,
        Boolean preferSulfateFree,
        Boolean preferSiliconeFree
) {
}
