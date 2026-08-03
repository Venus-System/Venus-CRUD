package com.venus.crud.mapper.user;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.user.UserPreferenceRequest;
import com.venus.crud.dto.response.user.UserPreferenceResponse;
import com.venus.crud.entity.user.User;
import com.venus.crud.entity.user.UserPreference;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface UserPreferenceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    UserPreference toEntity(UserPreferenceRequest request);

    @Mapping(target = "userId", source = "user.id")
    UserPreferenceResponse toResponse(UserPreference entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UserPreferenceRequest request, @MappingTarget UserPreference entity);

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}
