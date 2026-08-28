package com.venus.crud.mapper.scoring;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.scoring.ScoringModelPatchRequest;
import com.venus.crud.dto.request.scoring.ScoringModelRequest;
import com.venus.crud.dto.response.scoring.ScoringModelResponse;
import com.venus.crud.entity.scoring.ScoringModel;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface ScoringModelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ScoringModel toEntity(ScoringModelRequest request);

    ScoringModelResponse toResponse(ScoringModel entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(ScoringModelRequest request, @MappingTarget ScoringModel entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(ScoringModelPatchRequest request, @MappingTarget ScoringModel entity);
}
