package com.venus.crud.mapper.jpa.scoring;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.scoring.RecommendationPatchRequest;
import com.venus.crud.dto.jpa.request.scoring.RecommendationRequest;
import com.venus.crud.dto.jpa.response.scoring.RecommendationResponse;
import com.venus.crud.entity.product.ProductVersion;
import com.venus.crud.entity.scan.AnalysisResult;
import com.venus.crud.entity.scoring.Recommendation;
import com.venus.crud.entity.shared.ProfileTag;
import com.venus.crud.entity.user.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface RecommendationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "profileTag", source = "profileTagId")
    @Mapping(target = "productVersion", source = "productVersionId")
    @Mapping(target = "analysisResult", source = "analysisResultId")
    Recommendation toEntity(RecommendationRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "profileTagId", source = "profileTag.id")
    @Mapping(target = "productVersionId", source = "productVersion.id")
    @Mapping(target = "analysisResultId", source = "analysisResult.id")
    RecommendationResponse toResponse(Recommendation entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(RecommendationRequest request, @MappingTarget Recommendation entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "profileTag", source = "profileTagId")
    @Mapping(target = "productVersion", source = "productVersionId")
    @Mapping(target = "analysisResult", source = "analysisResultId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(RecommendationPatchRequest request, @MappingTarget Recommendation entity);

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }

    default ProfileTag mapProfileTag(Long profileTagId) {
        if (profileTagId == null) {
            return null;
        }
        ProfileTag profileTag = new ProfileTag();
        profileTag.setId(profileTagId);
        return profileTag;
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
}
