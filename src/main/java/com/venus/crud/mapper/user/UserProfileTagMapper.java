package com.venus.crud.mapper.user;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.user.UserProfileTagPatchRequest;
import com.venus.crud.dto.request.user.UserProfileTagRequest;
import com.venus.crud.dto.response.user.UserProfileTagResponse;
import com.venus.crud.entity.shared.ProfileTag;
import com.venus.crud.entity.user.User;
import com.venus.crud.entity.user.UserProfileTag;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = VenusMapperConfig.class)
public interface UserProfileTagMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "profileTag", source = "profileTagId")
    UserProfileTag toEntity(UserProfileTagRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "profileTagId", source = "profileTag.id")
    UserProfileTagResponse toResponse(UserProfileTag entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(UserProfileTagRequest request, @MappingTarget UserProfileTag entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "profileTag", source = "profileTagId")
    void patchEntity(UserProfileTagPatchRequest request, @MappingTarget UserProfileTag entity);

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }

    default ProfileTag mapProfileTag(Long profileTagId) {
        if (profileTagId == null) {
            return null;
        }
        ProfileTag profileTag = new ProfileTag();
        profileTag.setId(profileTagId);
        return profileTag;
    }
}
