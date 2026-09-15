package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.HairType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class HairTypeConverter implements AttributeConverter<HairType, String> {

    private static final String PREFIX = "TYPE_";

    @Override
    public String convertToDatabaseColumn(HairType attribute) {
        return attribute == null ? null : attribute.name().substring(PREFIX.length());
    }

    @Override
    public HairType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : HairType.valueOf(PREFIX + dbData);
    }
}
