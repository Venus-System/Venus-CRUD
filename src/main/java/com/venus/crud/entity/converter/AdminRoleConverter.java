package com.venus.crud.entity.converter;

import com.venus.crud.entity.enums.AdminRole;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AdminRoleConverter extends LowercaseEnumConverter<AdminRole> {
    public AdminRoleConverter() {
        super(AdminRole.class);
    }
}
