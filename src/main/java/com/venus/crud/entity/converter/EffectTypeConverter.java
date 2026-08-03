package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.EffectType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EffectTypeConverter extends LowercaseEnumConverter<EffectType> {
    public EffectTypeConverter() {
        super(EffectType.class);
    }
}
