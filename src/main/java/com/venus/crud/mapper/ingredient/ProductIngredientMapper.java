package com.venus.crud.mapper.ingredient;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.ingredient.ProductIngredientPatchRequest;
import com.venus.crud.dto.request.ingredient.ProductIngredientRequest;
import com.venus.crud.dto.response.ingredient.ProductIngredientResponse;
import com.venus.crud.entity.ingredient.Ingredient;
import com.venus.crud.entity.ingredient.ProductIngredient;
import com.venus.crud.entity.product.ProductVersion;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface ProductIngredientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "productVersion", source = "productVersionId")
    @Mapping(target = "ingredient", source = "ingredientId")
    ProductIngredient toEntity(ProductIngredientRequest request);

    @Mapping(target = "productVersionId", source = "productVersion.id")
    @Mapping(target = "ingredientId", source = "ingredient.id")
    ProductIngredientResponse toResponse(ProductIngredient entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(ProductIngredientRequest request, @MappingTarget ProductIngredient entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "productVersion", source = "productVersionId")
    @Mapping(target = "ingredient", source = "ingredientId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(ProductIngredientPatchRequest request, @MappingTarget ProductIngredient entity);

    default ProductVersion mapProductVersion(Long productVersionId) {
        if (productVersionId == null) {
            return null;
        }
        ProductVersion productVersion = new ProductVersion();
        productVersion.setId(productVersionId);
        return productVersion;
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
