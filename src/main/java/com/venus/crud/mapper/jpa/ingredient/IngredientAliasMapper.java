package com.venus.crud.mapper.jpa.ingredient;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.ingredient.IngredientAliasPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.IngredientAliasRequest;
import com.venus.crud.dto.jpa.response.ingredient.IngredientAliasResponse;
import com.venus.crud.entity.ingredient.Ingredient;
import com.venus.crud.entity.ingredient.IngredientAlias;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = VenusMapperConfig.class)
public interface IngredientAliasMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ingredient", source = "ingredientId")
    IngredientAlias toEntity(IngredientAliasRequest request);

    @Mapping(target = "ingredientId", source = "ingredient.id")
    IngredientAliasResponse toResponse(IngredientAlias entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(IngredientAliasRequest request, @MappingTarget IngredientAlias entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ingredient", source = "ingredientId")
    void patchEntity(IngredientAliasPatchRequest request, @MappingTarget IngredientAlias entity);

    default Ingredient mapIngredient(Long ingredientId) {
        if (ingredientId == null) {
            return null;
        }
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);
        return ingredient;
    }
}
