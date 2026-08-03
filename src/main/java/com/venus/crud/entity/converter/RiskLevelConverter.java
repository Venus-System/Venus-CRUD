package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.RiskLevel;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RiskLevelConverter extends LowercaseEnumConverter<RiskLevel> {
    public RiskLevelConverter() {
        super(RiskLevel.class);
    }
}
