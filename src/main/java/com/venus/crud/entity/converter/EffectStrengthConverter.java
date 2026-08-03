package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.EffectStrength;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EffectStrengthConverter extends LowercaseEnumConverter<EffectStrength> {
    public EffectStrengthConverter() {
        super(EffectStrength.class);
    }
}
