package com.venus.crud.mapper.product;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.product.ProductClaimRequest;
import com.venus.crud.dto.response.product.ProductClaimResponse;
import com.venus.crud.entity.product.Claim;
import com.venus.crud.entity.product.ProductClaim;
import com.venus.crud.entity.product.ProductVersion;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface ProductClaimMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "productVersion", source = "productVersionId")
    @Mapping(target = "claim", source = "claimId")
    ProductClaim toEntity(ProductClaimRequest request);

    @Mapping(target = "productVersionId", source = "productVersion.id")
    @Mapping(target = "claimId", source = "claim.id")
    ProductClaimResponse toResponse(ProductClaim entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ProductClaimRequest request, @MappingTarget ProductClaim entity);

    default ProductVersion mapProductVersion(Long productVersionId) {
        if (productVersionId == null) {
            return null;
        }
        ProductVersion productVersion = new ProductVersion();
        productVersion.setId(productVersionId);
        return productVersion;
    }

    default Claim mapClaim(Long claimId) {
        if (claimId == null) {
            return null;
        }
        Claim claim = new Claim();
        claim.setId(claimId);
        return claim;
    }
}
