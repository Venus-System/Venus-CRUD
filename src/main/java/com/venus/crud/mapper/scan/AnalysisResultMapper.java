package com.venus.crud.mapper.scan;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.scan.AnalysisResultRequest;
import com.venus.crud.dto.response.scan.AnalysisResultResponse;
import com.venus.crud.entity.product.ProductVersion;
import com.venus.crud.entity.scan.AnalysisResult;
import com.venus.crud.entity.scan.ScanSession;
import com.venus.crud.entity.scoring.ScoringModel;
import com.venus.crud.entity.user.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface AnalysisResultMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "scanSession", source = "scanSessionId")
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "productVersion", source = "productVersionId")
    @Mapping(target = "scoringModel", source = "scoringModelId")
    AnalysisResult toEntity(AnalysisResultRequest request);

    @Mapping(target = "scanSessionId", source = "scanSession.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "productVersionId", source = "productVersion.id")
    @Mapping(target = "scoringModelId", source = "scoringModel.id")
    AnalysisResultResponse toResponse(AnalysisResult entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(AnalysisResultRequest request, @MappingTarget AnalysisResult entity);

    default ScanSession mapScanSession(Long scanSessionId) {
        if (scanSessionId == null) {
            return null;
        }
        ScanSession scanSession = new ScanSession();
        scanSession.setId(scanSessionId);
        return scanSession;
    }

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }

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
