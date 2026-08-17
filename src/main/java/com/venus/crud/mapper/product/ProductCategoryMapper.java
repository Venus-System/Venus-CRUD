package com.venus.crud.mapper.product;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.product.ProductCategoryPatchRequest;
import com.venus.crud.dto.request.product.ProductCategoryRequest;
import com.venus.crud.dto.response.product.ProductCategoryResponse;
import com.venus.crud.entity.product.ProductCategory;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = VenusMapperConfig.class)
public interface ProductCategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductCategory toEntity(ProductCategoryRequest request);

    ProductCategoryResponse toResponse(ProductCategory entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(ProductCategoryRequest request, @MappingTarget ProductCategory entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void patchEntity(ProductCategoryPatchRequest request, @MappingTarget ProductCategory entity);
}
