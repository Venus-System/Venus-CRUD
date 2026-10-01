package com.venus.crud.mapper.jpa.review;

import static org.assertj.core.api.Assertions.assertThat;

import com.venus.crud.entity.review.Review;
import com.venus.crud.entity.user.User;
import org.junit.jupiter.api.Test;

class ReviewMapperTest {

    private final ReviewMapper mapper = new ReviewMapperImpl();

    @Test
    void authorNameIsTheFirstNameAndTheInitialOfTheLastWord() {
        assertThat(authorNameFor("Akira Kenji Tanaka")).isEqualTo("Akira T.");
        assertThat(authorNameFor("Maria da Silva")).isEqualTo("Maria S.");
        assertThat(authorNameFor("akira kenji")).isEqualTo("akira K.");
        assertThat(authorNameFor("Bia álvares")).isEqualTo("Bia Á.");
    }

    @Test
    void singleWordNameAndExtraSpacesKeepOnlyWhatExists() {
        assertThat(authorNameFor("Akira")).isEqualTo("Akira");
        assertThat(authorNameFor("  Ana   Souza  ")).isEqualTo("Ana S.");
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
