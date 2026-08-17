package com.venus.crud.mapper.product;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.product.ProductLabelPatchRequest;
import com.venus.crud.dto.request.product.ProductLabelRequest;
import com.venus.crud.dto.response.product.ProductLabelResponse;
import com.venus.crud.entity.product.ProductLabel;
import com.venus.crud.entity.product.ProductVersion;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = VenusMapperConfig.class)
public interface ProductLabelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "productVersion", source = "productVersionId")
    ProductLabel toEntity(ProductLabelRequest request);

    @Mapping(target = "productVersionId", source = "productVersion.id")
    ProductLabelResponse toResponse(ProductLabel entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(ProductLabelRequest request, @MappingTarget ProductLabel entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "productVersion", source = "productVersionId")
    void patchEntity(ProductLabelPatchRequest request, @MappingTarget ProductLabel entity);

    default ProductVersion mapProductVersion(Long productVersionId) {
        if (productVersionId == null) {
            return null;
        }
        ProductVersion productVersion = new ProductVersion();
        productVersion.setId(productVersionId);
        return productVersion;
    }
}
