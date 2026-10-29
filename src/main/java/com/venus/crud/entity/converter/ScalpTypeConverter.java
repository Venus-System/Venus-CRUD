package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.ScalpType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ScalpTypeConverter extends LowercaseEnumConverter<ScalpType> {
    public ScalpTypeConverter() {
        super(ScalpType.class);
    }
}
