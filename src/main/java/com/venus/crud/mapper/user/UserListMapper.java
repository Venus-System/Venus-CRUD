package com.venus.crud.mapper.user;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.user.UserListPatchRequest;
import com.venus.crud.dto.request.user.UserListRequest;
import com.venus.crud.dto.response.user.UserListResponse;
import com.venus.crud.entity.user.User;
import com.venus.crud.entity.user.UserList;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface UserListMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    UserList toEntity(UserListRequest request);

    @Mapping(target = "userId", source = "user.id")
    UserListResponse toResponse(UserList entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(UserListRequest request, @MappingTarget UserList entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(UserListPatchRequest request, @MappingTarget UserList entity);

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}
