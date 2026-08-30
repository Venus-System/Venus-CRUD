package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.product.BrandResponse;
import com.venus.crud.dto.jpa.response.product.PackagingResponse;
import com.venus.crud.dto.jpa.response.product.ProductCategoryResponse;
import com.venus.crud.dto.jpa.response.product.ProductLabelResponse;
import com.venus.crud.dto.jpa.response.product.ProductResponse;
import com.venus.crud.dto.jpa.response.product.ProductVersionResponse;
import java.util.List;

public record ProductFullResponse(
        ProductResponse product,
        BrandResponse brand,
        ProductCategoryResponse category,
        ProductVersionResponse currentVersion,
        PackagingResponse packaging,
        ProductLabelResponse label,
        List<ProductClaimDetailResponse> claims
) {
}
