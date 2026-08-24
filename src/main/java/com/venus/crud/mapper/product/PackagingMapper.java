package com.venus.crud.mapper.product;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.product.PackagingPatchRequest;
import com.venus.crud.dto.request.product.PackagingRequest;
import com.venus.crud.dto.response.product.PackagingResponse;
import com.venus.crud.entity.product.Packaging;
import com.venus.crud.entity.product.ProductVersion;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface PackagingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "productVersion", source = "productVersionId")
    Packaging toEntity(PackagingRequest request);

    @Mapping(target = "productVersionId", source = "productVersion.id")
    PackagingResponse toResponse(Packaging entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(PackagingRequest request, @MappingTarget Packaging entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "productVersion", source = "productVersionId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(PackagingPatchRequest request, @MappingTarget Packaging entity);

    default ProductVersion mapProductVersion(Long productVersionId) {
        if (productVersionId == null) {
            return null;
        }
        ProductVersion productVersion = new ProductVersion();
        productVersion.setId(productVersionId);
        return productVersion;
    }
}
