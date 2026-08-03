package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.ReportTargetType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ReportTargetTypeConverter extends LowercaseEnumConverter<ReportTargetType> {
    public ReportTargetTypeConverter() {
        super(ReportTargetType.class);
    }
}
