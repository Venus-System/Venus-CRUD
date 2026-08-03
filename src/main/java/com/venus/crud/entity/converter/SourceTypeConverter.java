package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.SourceType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SourceTypeConverter extends LowercaseEnumConverter<SourceType> {
    public SourceTypeConverter() {
        super(SourceType.class);
    }
}
