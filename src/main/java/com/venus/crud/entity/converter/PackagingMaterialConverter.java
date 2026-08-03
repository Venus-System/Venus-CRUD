package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.PackagingMaterial;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PackagingMaterialConverter extends LowercaseEnumConverter<PackagingMaterial> {
    public PackagingMaterialConverter() {
        super(PackagingMaterial.class);
    }
}
