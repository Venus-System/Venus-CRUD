package com.venus.crud.mapper.ingredient;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.ingredient.IngredientCategoryRequest;
import com.venus.crud.dto.response.ingredient.IngredientCategoryResponse;
import com.venus.crud.entity.ingredient.IngredientCategory;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface IngredientCategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    IngredientCategory toEntity(IngredientCategoryRequest request);

    IngredientCategoryResponse toResponse(IngredientCategory entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(IngredientCategoryRequest request, @MappingTarget IngredientCategory entity);
}
