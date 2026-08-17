package com.venus.crud.mapper.product;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.product.ProductImagePatchRequest;
import com.venus.crud.dto.request.product.ProductImageRequest;
import com.venus.crud.dto.response.product.ProductImageResponse;
import com.venus.crud.entity.product.ProductImage;
import com.venus.crud.entity.product.ProductVersion;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = VenusMapperConfig.class)
public interface ProductImageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "productVersion", source = "productVersionId")
    ProductImage toEntity(ProductImageRequest request);

    @Mapping(target = "productVersionId", source = "productVersion.id")
    ProductImageResponse toResponse(ProductImage entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(ProductImageRequest request, @MappingTarget ProductImage entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "productVersion", source = "productVersionId")
    void patchEntity(ProductImagePatchRequest request, @MappingTarget ProductImage entity);

    default ProductVersion mapProductVersion(Long productVersionId) {
        if (productVersionId == null) {
            return null;
        }
        ProductVersion productVersion = new ProductVersion();
        productVersion.setId(productVersionId);
        return productVersion;
    }
}
