package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.ScanStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ScanStatusConverter extends LowercaseEnumConverter<ScanStatus> {
    public ScanStatusConverter() {
        super(ScanStatus.class);
    }
}
