package com.venus.crud.mapper.scoring;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.scoring.ScoreCategoryRequest;
import com.venus.crud.dto.response.scoring.ScoreCategoryResponse;
import com.venus.crud.entity.scoring.ScoreCategory;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface ScoreCategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ScoreCategory toEntity(ScoreCategoryRequest request);

    ScoreCategoryResponse toResponse(ScoreCategory entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ScoreCategoryRequest request, @MappingTarget ScoreCategory entity);
}
