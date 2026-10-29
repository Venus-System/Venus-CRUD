package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.SensitivityLevel;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SensitivityLevelConverter extends LowercaseEnumConverter<SensitivityLevel> {
    public SensitivityLevelConverter() {
        super(SensitivityLevel.class);
    }
}
