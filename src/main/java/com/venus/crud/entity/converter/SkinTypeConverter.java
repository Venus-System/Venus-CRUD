package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.SkinType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SkinTypeConverter extends LowercaseEnumConverter<SkinType> {
    public SkinTypeConverter() {
        super(SkinType.class);
    }
}
