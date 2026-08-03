package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.ProfileTagCategory;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ProfileTagCategoryConverter extends LowercaseEnumConverter<ProfileTagCategory> {
    public ProfileTagCategoryConverter() {
        super(ProfileTagCategory.class);
    }
}
