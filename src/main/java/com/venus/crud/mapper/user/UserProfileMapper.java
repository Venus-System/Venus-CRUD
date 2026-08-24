package com.venus.crud.mapper.user;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.user.UserProfilePatchRequest;
import com.venus.crud.dto.request.user.UserProfileRequest;
import com.venus.crud.dto.response.user.UserProfileResponse;
import com.venus.crud.entity.user.User;
import com.venus.crud.entity.user.UserProfile;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface UserProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    UserProfile toEntity(UserProfileRequest request);

    @Mapping(target = "userId", source = "user.id")
    UserProfileResponse toResponse(UserProfile entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(UserProfileRequest request, @MappingTarget UserProfile entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(UserProfilePatchRequest request, @MappingTarget UserProfile entity);

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}
