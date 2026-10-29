package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.AnalysisStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AnalysisStatusConverter extends LowercaseEnumConverter<AnalysisStatus> {
    public AnalysisStatusConverter() {
        super(AnalysisStatus.class);
    }
}
