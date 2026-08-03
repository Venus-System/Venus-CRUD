package com.venus.crud.mapper.ingredient;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.ingredient.IngredientAliasRequest;
import com.venus.crud.dto.response.ingredient.IngredientAliasResponse;
import com.venus.crud.entity.ingredient.Ingredient;
import com.venus.crud.entity.ingredient.IngredientAlias;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

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
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(IngredientAliasRequest request, @MappingTarget IngredientAlias entity);

    default Ingredient mapIngredient(Long ingredientId) {
        if (ingredientId == null) {
            return null;
        }
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);
        return ingredient;
    }
}
