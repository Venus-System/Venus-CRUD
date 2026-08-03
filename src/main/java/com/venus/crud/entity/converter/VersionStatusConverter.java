package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.VersionStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VersionStatusConverter extends LowercaseEnumConverter<VersionStatus> {
    public VersionStatusConverter() {
        super(VersionStatus.class);
    }
}
