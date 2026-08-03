package com.venus.crud.mapper.user;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.user.RoutineRequest;
import com.venus.crud.dto.response.user.RoutineResponse;
import com.venus.crud.entity.user.Routine;
import com.venus.crud.entity.user.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface RoutineMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    Routine toEntity(RoutineRequest request);

    @Mapping(target = "userId", source = "user.id")
    RoutineResponse toResponse(Routine entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(RoutineRequest request, @MappingTarget Routine entity);

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}
