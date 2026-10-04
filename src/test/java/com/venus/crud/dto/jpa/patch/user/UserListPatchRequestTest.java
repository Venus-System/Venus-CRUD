package com.venus.crud.dto.jpa.patch.user;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;

class UserListPatchRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void descriptionLongerThan500CharactersIsRejected() {
        assertThat(validator.validate(patchWithDescription("a".repeat(501)))).hasSize(1);
    }

    @Test
    void descriptionWith500CharactersIsAccepted() {
        assertThat(validator.validate(patchWithDescription("a".repeat(500)))).isEmpty();
    }

    @Test
    void nullDescriptionIsAccepted() {
        assertThat(validator.validate(patchWithDescription(null))).isEmpty();
    }

    private UserListPatchRequest patchWithDescription(String description) {
        return new UserListPatchRequest(null, null, null, JsonNullable.of(description), JsonNullable.undefined());
    }
}
