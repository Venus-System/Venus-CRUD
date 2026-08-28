package com.venus.crud.mapper.jpa.ingredient;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.ingredient.IngredientPropertyPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.IngredientPropertyRequest;
import com.venus.crud.dto.jpa.response.ingredient.IngredientPropertyResponse;
import com.venus.crud.entity.ingredient.Ingredient;
import com.venus.crud.entity.ingredient.IngredientProperty;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = VenusMapperConfig.class)
public interface IngredientPropertyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ingredient", source = "ingredientId")
    IngredientProperty toEntity(IngredientPropertyRequest request);

    @Mapping(target = "ingredientId", source = "ingredient.id")
    IngredientPropertyResponse toResponse(IngredientProperty entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(IngredientPropertyRequest request, @MappingTarget IngredientProperty entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ingredient", source = "ingredientId")
    void patchEntity(IngredientPropertyPatchRequest request, @MappingTarget IngredientProperty entity);

    default Ingredient mapIngredient(Long ingredientId) {
        if (ingredientId == null) {
            return null;
        }
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);
        return ingredient;
    }
}
