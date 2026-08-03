package com.venus.crud.mapper.scan;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.scan.ScanSessionRequest;
import com.venus.crud.dto.response.scan.ScanSessionResponse;
import com.venus.crud.entity.scan.ScanSession;
import com.venus.crud.entity.user.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface ScanSessionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    ScanSession toEntity(ScanSessionRequest request);

    @Mapping(target = "userId", source = "user.id")
    ScanSessionResponse toResponse(ScanSession entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ScanSessionRequest request, @MappingTarget ScanSession entity);

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}
