package com.venus.crud.mapper.jpa.ingredient;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.ingredient.AllergyIngredientPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.AllergyIngredientRequest;
import com.venus.crud.dto.jpa.response.ingredient.AllergyIngredientResponse;
import com.venus.crud.entity.ingredient.AllergyIngredient;
import com.venus.crud.entity.ingredient.Ingredient;
import com.venus.crud.entity.user.Allergy;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface AllergyIngredientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "allergy", source = "allergyId")
    @Mapping(target = "ingredient", source = "ingredientId")
    AllergyIngredient toEntity(AllergyIngredientRequest request);

    @Mapping(target = "allergyId", source = "allergy.id")
    @Mapping(target = "ingredientId", source = "ingredient.id")
    AllergyIngredientResponse toResponse(AllergyIngredient entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(AllergyIngredientRequest request, @MappingTarget AllergyIngredient entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "allergy", source = "allergyId")
    @Mapping(target = "ingredient", source = "ingredientId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(AllergyIngredientPatchRequest request, @MappingTarget AllergyIngredient entity);

    default Allergy mapAllergy(Long allergyId) {
        if (allergyId == null) {
            return null;
        }
        Allergy allergy = new Allergy();
        allergy.setId(allergyId);
        return allergy;
    }

    default Ingredient mapIngredient(Long ingredientId) {
        if (ingredientId == null) {
            return null;
        }
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);
        return ingredient;
    }
}
