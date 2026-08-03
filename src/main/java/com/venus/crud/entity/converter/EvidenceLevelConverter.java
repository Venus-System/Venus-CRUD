package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.EvidenceLevel;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EvidenceLevelConverter extends LowercaseEnumConverter<EvidenceLevel> {
    public EvidenceLevelConverter() {
        super(EvidenceLevel.class);
    }
}
