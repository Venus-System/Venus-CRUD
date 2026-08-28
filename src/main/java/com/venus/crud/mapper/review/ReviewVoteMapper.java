package com.venus.crud.mapper.review;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.request.review.ReviewVotePatchRequest;
import com.venus.crud.dto.request.review.ReviewVoteRequest;
import com.venus.crud.dto.response.review.ReviewVoteResponse;
import com.venus.crud.entity.review.Review;
import com.venus.crud.entity.review.ReviewVote;
import com.venus.crud.entity.user.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface ReviewVoteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "review", source = "reviewId")
    @Mapping(target = "user", source = "userId")
    ReviewVote toEntity(ReviewVoteRequest request);

    @Mapping(target = "reviewId", source = "review.id")
    @Mapping(target = "userId", source = "user.id")
    ReviewVoteResponse toResponse(ReviewVote entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(ReviewVoteRequest request, @MappingTarget ReviewVote entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "review", source = "reviewId")
    @Mapping(target = "user", source = "userId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(ReviewVotePatchRequest request, @MappingTarget ReviewVote entity);

    default Review mapReview(Long reviewId) {
        if (reviewId == null) {
            return null;
        }
        Review review = new Review();
        review.setId(reviewId);
        return review;
    }

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
}
