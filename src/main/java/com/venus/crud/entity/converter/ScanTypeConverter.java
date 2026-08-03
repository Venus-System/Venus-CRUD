package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.ScanType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ScanTypeConverter extends LowercaseEnumConverter<ScanType> {
    public ScanTypeConverter() {
        super(ScanType.class);
    }
}
