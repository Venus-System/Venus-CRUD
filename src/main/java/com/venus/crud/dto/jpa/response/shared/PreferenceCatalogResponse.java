package com.venus.crud.dto.jpa.response.shared;

import com.venus.crud.entity.enums.ProfileTagCategory;

public record PreferenceCatalogResponse(
        Long id,
        String slug,
        String name,
        String description,
        ProfileTagCategory category,
        String userPreferenceField
) {
}
