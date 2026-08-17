package com.venus.crud.mapper.shared;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.shared.ProfileTagPatchRequest;
import com.venus.crud.dto.request.shared.ProfileTagRequest;
import com.venus.crud.dto.response.shared.ProfileTagResponse;
import com.venus.crud.entity.shared.ProfileTag;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = VenusMapperConfig.class)
public interface ProfileTagMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProfileTag toEntity(ProfileTagRequest request);

    ProfileTagResponse toResponse(ProfileTag entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(ProfileTagRequest request, @MappingTarget ProfileTag entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void patchEntity(ProfileTagPatchRequest request, @MappingTarget ProfileTag entity);
}
