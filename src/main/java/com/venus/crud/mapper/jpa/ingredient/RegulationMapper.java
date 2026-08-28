package com.venus.crud.mapper.jpa.ingredient;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.ingredient.RegulationPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.RegulationRequest;
import com.venus.crud.dto.jpa.response.ingredient.RegulationResponse;
import com.venus.crud.entity.ingredient.Regulation;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = VenusMapperConfig.class)
public interface RegulationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Regulation toEntity(RegulationRequest request);

    RegulationResponse toResponse(Regulation entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(RegulationRequest request, @MappingTarget Regulation entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void patchEntity(RegulationPatchRequest request, @MappingTarget Regulation entity);
}
