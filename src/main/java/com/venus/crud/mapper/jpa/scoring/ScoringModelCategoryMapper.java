package com.venus.crud.mapper.jpa.scoring;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.scoring.ScoringModelCategoryPatchRequest;
import com.venus.crud.dto.jpa.request.scoring.ScoringModelCategoryRequest;
import com.venus.crud.dto.jpa.response.scoring.ScoringModelCategoryResponse;
import com.venus.crud.entity.scoring.ScoreCategory;
import com.venus.crud.entity.scoring.ScoringModel;
import com.venus.crud.entity.scoring.ScoringModelCategory;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface ScoringModelCategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "scoringModel", source = "scoringModelId")
    @Mapping(target = "scoreCategory", source = "scoreCategoryId")
    ScoringModelCategory toEntity(ScoringModelCategoryRequest request);

    @Mapping(target = "scoringModelId", source = "scoringModel.id")
    @Mapping(target = "scoreCategoryId", source = "scoreCategory.id")
    ScoringModelCategoryResponse toResponse(ScoringModelCategory entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "scoringModel", source = "scoringModelId")
    @Mapping(target = "scoreCategory", source = "scoreCategoryId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(ScoringModelCategoryPatchRequest request, @MappingTarget ScoringModelCategory entity);

    default ScoringModel mapScoringModel(Long scoringModelId) {
        if (scoringModelId == null) {
            return null;
        }
        ScoringModel scoringModel = new ScoringModel();
        scoringModel.setId(scoringModelId);
        return scoringModel;
    }

    default ScoreCategory mapScoreCategory(Long scoreCategoryId) {
        if (scoreCategoryId == null) {
            return null;
        }
        ScoreCategory scoreCategory = new ScoreCategory();
        scoreCategory.setId(scoreCategoryId);
        return scoreCategory;
    }
}
