package com.venus.crud.mapper.scoring;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.scoring.ProductScoreRequest;
import com.venus.crud.dto.response.scoring.ProductScoreResponse;
import com.venus.crud.entity.product.ProductVersion;
import com.venus.crud.entity.scoring.ProductScore;
import com.venus.crud.entity.scoring.ScoringModel;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface ProductScoreMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "productVersion", source = "productVersionId")
    @Mapping(target = "scoringModel", source = "scoringModelId")
    ProductScore toEntity(ProductScoreRequest request);

    @Mapping(target = "productVersionId", source = "productVersion.id")
    @Mapping(target = "scoringModelId", source = "scoringModel.id")
    ProductScoreResponse toResponse(ProductScore entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ProductScoreRequest request, @MappingTarget ProductScore entity);

    default ProductVersion mapProductVersion(Long productVersionId) {
        if (productVersionId == null) {
            return null;
        }
        ProductVersion productVersion = new ProductVersion();
        productVersion.setId(productVersionId);
        return productVersion;
    }

    default ScoringModel mapScoringModel(Long scoringModelId) {
        if (scoringModelId == null) {
            return null;
        }
        ScoringModel scoringModel = new ScoringModel();
        scoringModel.setId(scoringModelId);
        return scoringModel;
    }
}
