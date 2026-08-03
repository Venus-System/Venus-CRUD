package com.venus.crud.mapper.scan;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.scan.RuleEvaluationRequest;
import com.venus.crud.dto.response.scan.RuleEvaluationResponse;
import com.venus.crud.entity.ingredient.CompatibilityRule;
import com.venus.crud.entity.ingredient.Ingredient;
import com.venus.crud.entity.scan.AnalysisResult;
import com.venus.crud.entity.scan.RuleEvaluation;
import com.venus.crud.entity.shared.ProfileTag;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface RuleEvaluationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "analysisResult", source = "analysisResultId")
    @Mapping(target = "compatibilityRule", source = "compatibilityRuleId")
    @Mapping(target = "ingredient", source = "ingredientId")
    @Mapping(target = "profileTag", source = "profileTagId")
    RuleEvaluation toEntity(RuleEvaluationRequest request);

    @Mapping(target = "analysisResultId", source = "analysisResult.id")
    @Mapping(target = "compatibilityRuleId", source = "compatibilityRule.id")
    @Mapping(target = "ingredientId", source = "ingredient.id")
    @Mapping(target = "profileTagId", source = "profileTag.id")
    RuleEvaluationResponse toResponse(RuleEvaluation entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(RuleEvaluationRequest request, @MappingTarget RuleEvaluation entity);

    default AnalysisResult mapAnalysisResult(Long analysisResultId) {
        if (analysisResultId == null) {
            return null;
        }
        AnalysisResult analysisResult = new AnalysisResult();
        analysisResult.setId(analysisResultId);
        return analysisResult;
    }

    default CompatibilityRule mapCompatibilityRule(Long compatibilityRuleId) {
        if (compatibilityRuleId == null) {
            return null;
        }
        CompatibilityRule compatibilityRule = new CompatibilityRule();
        compatibilityRule.setId(compatibilityRuleId);
        return compatibilityRule;
    }

    default Ingredient mapIngredient(Long ingredientId) {
        if (ingredientId == null) {
            return null;
        }
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);
        return ingredient;
    }

    default ProfileTag mapProfileTag(Long profileTagId) {
        if (profileTagId == null) {
            return null;
        }
        ProfileTag profileTag = new ProfileTag();
        profileTag.setId(profileTagId);
        return profileTag;
    }
}
