package com.venus.crud.mapper.jpa.review;

import static org.assertj.core.api.Assertions.assertThat;

import com.venus.crud.entity.review.Review;
import com.venus.crud.entity.user.User;
import org.junit.jupiter.api.Test;

class ReviewMapperTest {

    private final ReviewMapper mapper = new ReviewMapperImpl();

    @Test
    void authorNameIsTheFirstNameAndTheInitialOfTheLastWord() {
        assertThat(authorNameFor("Henrique Akira")).isEqualTo("Henrique A.");
        assertThat(authorNameFor("Sophia de Castro")).isEqualTo("Sophia C.");
        assertThat(authorNameFor("Rafael Lopes")).isEqualTo("Rafael L.");
        assertThat(authorNameFor("Laura Gomes")).isEqualTo("Laura G.");
    }

    @Test
    void singleWordNameAndExtraSpacesKeepOnlyWhatExists() {
        assertThat(authorNameFor("Matheus Oresteszinho")).isEqualTo("Matheus O.");
        assertThat(authorNameFor("  Felipe   Augusto  ")).isEqualTo("Felipe A.");
    }

    @Test
    void blankNameGivesNoAuthorName() {
        assertThat(authorNameFor("   ")).isNull();
    }

    private String authorNameFor(String fullName) {
        User user = new User();
        user.setName(fullName);
        Review review = new Review();
        review.setUser(user);
        return mapper.toResponse(review).authorName();
    }
}
