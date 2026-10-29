package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.ReportStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ReportStatusConverter extends LowercaseEnumConverter<ReportStatus> {
    public ReportStatusConverter() {
        super(ReportStatus.class);
    }
}
