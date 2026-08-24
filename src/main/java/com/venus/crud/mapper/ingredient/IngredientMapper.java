package com.venus.crud.mapper.ingredient;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.ingredient.IngredientPatchRequest;
import com.venus.crud.dto.request.ingredient.IngredientRequest;
import com.venus.crud.dto.response.ingredient.IngredientResponse;
import com.venus.crud.entity.ingredient.Ingredient;
import com.venus.crud.entity.ingredient.IngredientCategory;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface IngredientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ingredientCategory", source = "ingredientCategoryId")
    Ingredient toEntity(IngredientRequest request);

    @Mapping(target = "ingredientCategoryId", source = "ingredientCategory.id")
    IngredientResponse toResponse(Ingredient entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(IngredientRequest request, @MappingTarget Ingredient entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ingredientCategory", source = "ingredientCategoryId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(IngredientPatchRequest request, @MappingTarget Ingredient entity);

    default IngredientCategory mapIngredientCategory(Long ingredientCategoryId) {
        if (ingredientCategoryId == null) {
            return null;
        }
        IngredientCategory ingredientCategory = new IngredientCategory();
        ingredientCategory.setId(ingredientCategoryId);
        return ingredientCategory;
    }
}
