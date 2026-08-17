package com.venus.crud.mapper.scoring;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.scoring.ScoreCategoryPatchRequest;
import com.venus.crud.dto.request.scoring.ScoreCategoryRequest;
import com.venus.crud.dto.response.scoring.ScoreCategoryResponse;
import com.venus.crud.entity.scoring.ScoreCategory;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = VenusMapperConfig.class)
public interface ScoreCategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ScoreCategory toEntity(ScoreCategoryRequest request);

    ScoreCategoryResponse toResponse(ScoreCategory entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(ScoreCategoryRequest request, @MappingTarget ScoreCategory entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void patchEntity(ScoreCategoryPatchRequest request, @MappingTarget ScoreCategory entity);
}
