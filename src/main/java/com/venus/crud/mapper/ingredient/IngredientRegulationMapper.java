package com.venus.crud.mapper.ingredient;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.ingredient.IngredientRegulationRequest;
import com.venus.crud.dto.response.ingredient.IngredientRegulationResponse;
import com.venus.crud.entity.ingredient.Ingredient;
import com.venus.crud.entity.ingredient.IngredientRegulation;
import com.venus.crud.entity.ingredient.Regulation;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface IngredientRegulationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ingredient", source = "ingredientId")
    @Mapping(target = "regulation", source = "regulationId")
    IngredientRegulation toEntity(IngredientRegulationRequest request);

    @Mapping(target = "ingredientId", source = "ingredient.id")
    @Mapping(target = "regulationId", source = "regulation.id")
    IngredientRegulationResponse toResponse(IngredientRegulation entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(IngredientRegulationRequest request, @MappingTarget IngredientRegulation entity);

    default Ingredient mapIngredient(Long ingredientId) {
        if (ingredientId == null) {
            return null;
        }
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);
        return ingredient;
    }

    default Regulation mapRegulation(Long regulationId) {
        if (regulationId == null) {
            return null;
        }
        Regulation regulation = new Regulation();
        regulation.setId(regulationId);
        return regulation;
    }
}
