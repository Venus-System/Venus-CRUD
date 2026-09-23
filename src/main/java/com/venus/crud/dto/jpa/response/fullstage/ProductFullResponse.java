package com.venus.crud.dto.jpa.response.fullstage;

import com.venus.crud.dto.jpa.response.media.MediaAssetResponse;
import com.venus.crud.dto.jpa.response.product.BrandResponse;
import com.venus.crud.dto.jpa.response.product.PackagingResponse;
import com.venus.crud.dto.jpa.response.product.ProductCategoryResponse;
import com.venus.crud.dto.jpa.response.product.ProductLabelResponse;
import com.venus.crud.dto.jpa.response.product.ProductResponse;
import com.venus.crud.dto.jpa.response.product.ProductVersionResponse;
import com.venus.crud.dto.jpa.response.scoring.ProductScoreResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ProductFullResponse(
        @Schema(description = "Produto referenciado.")
        ProductResponse product,
        @Schema(description = "Marca do produto.")
        BrandResponse brand,
        @Schema(description = "Categoria a que o registro pertence.")
        ProductCategoryResponse category,
        @Schema(description = "Versão vigente do produto.")
        ProductVersionResponse currentVersion,
        @Schema(description = "Fotos desta versão do produto.")
        List<MediaAssetResponse> photos,
        @Schema(description = "Embalagem desta versão do produto.")
        PackagingResponse packaging,
        @Schema(description = "Rótulo capturado do produto.")
        ProductLabelResponse label,
        @Schema(description = "Alegações declaradas por esta versão do produto.")
        List<ProductClaimDetailResponse> claims,
        @Schema(description = "Score do produto.")
        ProductScoreResponse score
) {
}
