package com.venus.crud.mapper.jpa.review;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.review.ReviewPatchRequest;
import com.venus.crud.dto.jpa.request.review.ReviewRequest;
import com.venus.crud.dto.jpa.response.review.ReviewResponse;
import com.venus.crud.entity.product.ProductVersion;
import com.venus.crud.entity.review.Review;
import com.venus.crud.entity.user.User;
import java.util.Locale;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
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
    @Mapping(target = "authorName", source = "user.name", qualifiedByName = "shortName")
    @Mapping(target = "productVersionId", source = "productVersion.id")
    ReviewResponse toResponse(Review entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(ReviewRequest request, @MappingTarget Review entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "productVersion", source = "productVersionId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(ReviewPatchRequest request, @MappingTarget Review entity);

    @Named("shortName")
    default String shortName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return null;
        }
        String[] words = fullName.strip().split("\\s+");
        String firstName = words[0];
        if (words.length == 1) {
            return firstName;
        }
        String lastName = words[words.length - 1];
        return firstName + " " + lastName.substring(0, 1).toUpperCase(Locale.ROOT) + ".";
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
}
