package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.RegulationStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RegulationStatusConverter extends LowercaseEnumConverter<RegulationStatus> {
    public RegulationStatusConverter() {
        super(RegulationStatus.class);
    }
}
