package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.PackagingFormat;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PackagingFormatConverter extends LowercaseEnumConverter<PackagingFormat> {
    public PackagingFormatConverter() {
        super(PackagingFormat.class);
    }
}
