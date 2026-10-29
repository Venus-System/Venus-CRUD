package com.venus.crud.service.jpa.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.venus.crud.dto.jpa.request.review.ReviewRequest;
import com.venus.crud.dto.jpa.response.review.ReviewResponse;
import com.venus.crud.entity.review.Review;
import com.venus.crud.entity.user.User;
import com.venus.crud.mapper.jpa.review.ReviewMapperImpl;
import com.venus.crud.repository.jpa.review.ReviewRepository;
import com.venus.crud.repository.jpa.user.UserRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    private ReviewService service;

    @BeforeEach
    void setUp() {
        service = new ReviewService(reviewRepository, userRepository, new ReviewMapperImpl());
    }

    @Test
    void createdReviewAlreadyComesWithTheAuthorName() {
        ReviewRequest request = new ReviewRequest(10L, 31L, new BigDecimal("4.5"), "Ótimo", "Gostei muito.", true);
        when(reviewRepository.findByUserIdAndProductVersionId(10L, 31L)).thenReturn(Optional.empty());
        when(userRepository.getReferenceById(10L)).thenReturn(user(10L, "Felipe Augusto"));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewResponse created = service.create(request);

        assertThat(created.authorName()).isEqualTo("Felipe A.");
        assertThat(created.usefulVotes()).isZero();
    }

    private User user(Long id, String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        return user;
    }
}
