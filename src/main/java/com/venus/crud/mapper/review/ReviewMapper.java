package com.venus.crud.mapper.review;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.review.ReviewRequest;
import com.venus.crud.dto.response.review.ReviewResponse;
import com.venus.crud.entity.product.ProductVersion;
import com.venus.crud.entity.review.Review;
import com.venus.crud.entity.user.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "productVersion", source = "productVersionId")
    Review toEntity(ReviewRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "productVersionId", source = "productVersion.id")
    ReviewResponse toResponse(Review entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ReviewRequest request, @MappingTarget Review entity);

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
}
