package com.venus.crud.dto.jpa.patch.user;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class UserPatchRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void latitudeInRangeIsAccepted() {
        assertThat(validator.validate(patchWithLatitude("90"))).isEmpty();
        assertThat(validator.validate(patchWithLatitude("0"))).isEmpty();
        assertThat(validator.validate(patchWithLatitude("-90"))).isEmpty();
    }

    @Test
    void latitudeOutOfRangeIsRejected() {
        assertThat(validator.validate(patchWithLatitude("90.01"))).hasSize(1);
        assertThat(validator.validate(patchWithLatitude("-90.01"))).hasSize(1);
    }

    @Test
    void longitudeInRangeIsAccepted() {
        assertThat(validator.validate(patchWithLongitude("180"))).isEmpty();
        assertThat(validator.validate(patchWithLongitude("0"))).isEmpty();
        assertThat(validator.validate(patchWithLongitude("-180"))).isEmpty();
    }

    @Test
    void longitudeOutOfRangeIsRejected() {
        assertThat(validator.validate(patchWithLongitude("180.01"))).hasSize(1);
        assertThat(validator.validate(patchWithLongitude("-180.01"))).hasSize(1);
    }

    @Test
    void cityAndStateAtLimitAreAccepted() {
        assertThat(validator.validate(patchWithCityAndState("a".repeat(100), "b".repeat(100)))).isEmpty();
    }

    @Test
    void longCityIsRejected() {
        assertThat(validator.validate(patchWithCityAndState("a".repeat(101), "Sao Paulo"))).hasSize(1);
    }

    @Test
    void longStateIsRejected() {
        assertThat(validator.validate(patchWithCityAndState("Sao Paulo", "b".repeat(101)))).hasSize(1);
    }

    @Test
    void emptyLocationIsAccepted() {
        assertThat(validator.validate(patchWithCityAndState(null, null))).isEmpty();
    }

    private UserPatchRequest patchWithLatitude(String latitude) {
        return new UserPatchRequest(null, null, null, null, null, new BigDecimal(latitude), null, null, null);
    }

    private UserPatchRequest patchWithLongitude(String longitude) {
        return new UserPatchRequest(null, null, null, null, null, null, new BigDecimal(longitude), null, null);
    }

    private UserPatchRequest patchWithCityAndState(String city, String state) {
        return new UserPatchRequest(null, null, null, null, null, null, null, city, state);
    }
}
