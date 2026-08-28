package com.venus.crud.mapper.jpa.user;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.user.UserPatchRequest;
import com.venus.crud.dto.jpa.request.user.UserRequest;
import com.venus.crud.dto.jpa.response.user.UserResponse;
import com.venus.crud.entity.user.User;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = VenusMapperConfig.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(UserRequest request);

    UserResponse toResponse(User entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(UserRequest request, @MappingTarget User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "firebaseUid", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    void patchEntity(UserPatchRequest request, @MappingTarget User entity);
}
