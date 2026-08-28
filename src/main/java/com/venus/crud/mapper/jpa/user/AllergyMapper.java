package com.venus.crud.mapper.jpa.user;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.user.AllergyPatchRequest;
import com.venus.crud.dto.jpa.request.user.AllergyRequest;
import com.venus.crud.dto.jpa.response.user.AllergyResponse;
import com.venus.crud.entity.user.Allergy;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = VenusMapperConfig.class)
public interface AllergyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Allergy toEntity(AllergyRequest request);

    AllergyResponse toResponse(Allergy entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(AllergyRequest request, @MappingTarget Allergy entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void patchEntity(AllergyPatchRequest request, @MappingTarget Allergy entity);
}
