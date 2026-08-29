package com.venus.crud.mapper.jpa.scan;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.scan.PersonalizedScorePatchRequest;
import com.venus.crud.dto.jpa.request.scan.PersonalizedScoreRequest;
import com.venus.crud.dto.jpa.response.scan.PersonalizedScoreResponse;
import com.venus.crud.entity.product.ProductVersion;
import com.venus.crud.entity.scan.AnalysisResult;
import com.venus.crud.entity.scan.PersonalizedScore;
import com.venus.crud.entity.scoring.ScoringModel;
import com.venus.crud.entity.user.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface PersonalizedScoreMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "productVersion", source = "productVersionId")
    @Mapping(target = "analysisResult", source = "analysisResultId")
    @Mapping(target = "scoringModel", source = "scoringModelId")
    PersonalizedScore toEntity(PersonalizedScoreRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "productVersionId", source = "productVersion.id")
    @Mapping(target = "analysisResultId", source = "analysisResult.id")
    @Mapping(target = "scoringModelId", source = "scoringModel.id")
    PersonalizedScoreResponse toResponse(PersonalizedScore entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(PersonalizedScoreRequest request, @MappingTarget PersonalizedScore entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "productVersion", source = "productVersionId")
    @Mapping(target = "analysisResult", source = "analysisResultId")
    @Mapping(target = "scoringModel", source = "scoringModelId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(PersonalizedScorePatchRequest request, @MappingTarget PersonalizedScore entity);

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

    default AnalysisResult mapAnalysisResult(Long analysisResultId) {
        if (analysisResultId == null) {
            return null;
        }
        AnalysisResult analysisResult = new AnalysisResult();
        analysisResult.setId(analysisResultId);
        return analysisResult;
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
