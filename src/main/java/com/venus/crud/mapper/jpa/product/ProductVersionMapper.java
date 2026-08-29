package com.venus.crud.mapper.jpa.product;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.product.ProductVersionPatchRequest;
import com.venus.crud.dto.jpa.request.product.ProductVersionRequest;
import com.venus.crud.dto.jpa.response.product.ProductVersionResponse;
import com.venus.crud.entity.product.Product;
import com.venus.crud.entity.product.ProductVersion;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface ProductVersionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "product", source = "productId")
    ProductVersion toEntity(ProductVersionRequest request);

    @Mapping(target = "productId", source = "product.id")
    ProductVersionResponse toResponse(ProductVersion entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(ProductVersionRequest request, @MappingTarget ProductVersion entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "product", source = "productId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(ProductVersionPatchRequest request, @MappingTarget ProductVersion entity);

    default Product mapProduct(Long productId) {
        if (productId == null) {
            return null;
        }
        Product product = new Product();
        product.setId(productId);
        return product;
    }
}
