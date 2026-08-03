package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.ImageRole;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ImageRoleConverter extends LowercaseEnumConverter<ImageRole> {
    public ImageRoleConverter() {
        super(ImageRole.class);
    }
}
