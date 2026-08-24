package com.venus.crud.mapper.user;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.user.AllergyPatchRequest;
import com.venus.crud.dto.request.user.AllergyRequest;
import com.venus.crud.dto.response.user.AllergyResponse;
import com.venus.crud.entity.user.Allergy;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

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
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(AllergyPatchRequest request, @MappingTarget Allergy entity);
}
