package com.venus.crud.mapper.jpa.ingredient;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.ingredient.IngredientCategoryPatchRequest;
import com.venus.crud.dto.jpa.request.ingredient.IngredientCategoryRequest;
import com.venus.crud.dto.jpa.response.ingredient.IngredientCategoryResponse;
import com.venus.crud.entity.ingredient.IngredientCategory;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = VenusMapperConfig.class)
public interface IngredientCategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    IngredientCategory toEntity(IngredientCategoryRequest request);

    IngredientCategoryResponse toResponse(IngredientCategory entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(IngredientCategoryRequest request, @MappingTarget IngredientCategory entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void patchEntity(IngredientCategoryPatchRequest request, @MappingTarget IngredientCategory entity);
}
