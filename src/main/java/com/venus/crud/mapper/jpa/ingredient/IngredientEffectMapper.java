package com.venus.crud.mapper.jpa.ingredient;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.ingredient.IngredientEffectPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.IngredientEffectRequest;
import com.venus.crud.dto.jpa.response.ingredient.IngredientEffectResponse;
import com.venus.crud.entity.ingredient.Ingredient;
import com.venus.crud.entity.ingredient.IngredientEffect;
import com.venus.crud.entity.shared.ProfileTag;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface IngredientEffectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ingredient", source = "ingredientId")
    @Mapping(target = "profileTag", source = "profileTagId")
    IngredientEffect toEntity(IngredientEffectRequest request);

    @Mapping(target = "ingredientId", source = "ingredient.id")
    @Mapping(target = "profileTagId", source = "profileTag.id")
    IngredientEffectResponse toResponse(IngredientEffect entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(IngredientEffectRequest request, @MappingTarget IngredientEffect entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ingredient", source = "ingredientId")
    @Mapping(target = "profileTag", source = "profileTagId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(IngredientEffectPatchRequest request, @MappingTarget IngredientEffect entity);

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
