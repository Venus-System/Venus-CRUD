package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.HairType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class HairTypeConverter extends LowercaseEnumConverter<HairType> {
    public HairTypeConverter() {
        super(HairType.class);
    }
}
