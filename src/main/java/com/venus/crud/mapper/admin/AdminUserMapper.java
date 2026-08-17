package com.venus.crud.mapper.admin;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.admin.AdminUserPatchRequest;
import com.venus.crud.dto.request.admin.AdminUserRequest;
import com.venus.crud.dto.response.admin.AdminUserResponse;
import com.venus.crud.entity.admin.AdminUser;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = VenusMapperConfig.class)
public interface AdminUserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AdminUser toEntity(AdminUserRequest request);

    AdminUserResponse toResponse(AdminUser entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(AdminUserRequest request, @MappingTarget AdminUser entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void patchEntity(AdminUserPatchRequest request, @MappingTarget AdminUser entity);
}
