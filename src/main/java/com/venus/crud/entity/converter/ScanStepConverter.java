package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.ScanStep;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ScanStepConverter extends LowercaseEnumConverter<ScanStep> {
    public ScanStepConverter() {
        super(ScanStep.class);
    }
}
