package com.venus.crud.mapper.jpa.ingredient;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.ingredient.CompatibilityRulePatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.CompatibilityRuleRequest;
import com.venus.crud.dto.jpa.response.ingredient.CompatibilityRuleResponse;
import com.venus.crud.entity.ingredient.CompatibilityRule;
import com.venus.crud.entity.ingredient.IngredientEffect;
import com.venus.crud.entity.scoring.ScoringModel;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface CompatibilityRuleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ingredientEffect", source = "ingredientEffectId")
    @Mapping(target = "scoringModel", source = "scoringModelId")
    CompatibilityRule toEntity(CompatibilityRuleRequest request);

    @Mapping(target = "ingredientEffectId", source = "ingredientEffect.id")
    @Mapping(target = "scoringModelId", source = "scoringModel.id")
    CompatibilityRuleResponse toResponse(CompatibilityRule entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(CompatibilityRuleRequest request, @MappingTarget CompatibilityRule entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ingredientEffect", source = "ingredientEffectId")
    @Mapping(target = "scoringModel", source = "scoringModelId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(CompatibilityRulePatchRequest request, @MappingTarget CompatibilityRule entity);

    default IngredientEffect mapIngredientEffect(Long ingredientEffectId) {
        if (ingredientEffectId == null) {
            return null;
        }
        IngredientEffect ingredientEffect = new IngredientEffect();
        ingredientEffect.setId(ingredientEffectId);
        return ingredientEffect;
    }

    default ScoringModel mapScoringModel(Long scoringModelId) {
        if (scoringModelId == null) {
            return null;
        }
        ScoringModel scoringModel = new ScoringModel();
        scoringModel.setId(scoringModelId);
        return scoringModel;
    }
}
