package com.venus.crud.mapper.jpa.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.venus.crud.dto.jpa.patch.user.UserPatchRequest;
import com.venus.crud.dto.jpa.request.user.UserRequest;
import com.venus.crud.dto.jpa.response.user.UserResponse;
import com.venus.crud.entity.enums.UserStatus;
import com.venus.crud.entity.user.User;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class UserMapperTest {

    private final UserMapper mapper = new UserMapperImpl();

    @Test
    void patchSavesLocation() {
        User user = userInSaoPaulo();

        mapper.patchEntity(patchWithLocation("-22.90", "-43.17", "Rio de Janeiro", "Rio de Janeiro"), user);

        assertThat(user.getLatitude()).isEqualByComparingTo("-22.90");
        assertThat(user.getLongitude()).isEqualByComparingTo("-43.17");
        assertThat(user.getCity()).isEqualTo("Rio de Janeiro");
        assertThat(user.getState()).isEqualTo("Rio de Janeiro");
    }

    @Test
    void patchKeepsOldLocation() {
        User user = userInSaoPaulo();

        mapper.patchEntity(new UserPatchRequest("Ana Souza", null, null, null, null, null, null, null, null), user);

        assertThat(user.getName()).isEqualTo("Ana Souza");
        assertThat(user.getLatitude()).isEqualByComparingTo("-23.55");
        assertThat(user.getLongitude()).isEqualByComparingTo("-46.63");
        assertThat(user.getCity()).isEqualTo("Sao Paulo");
        assertThat(user.getState()).isEqualTo("Sao Paulo");
    }

    @Test
    void patchRoundsToTwoPlaces() {
        User user = new User();

        mapper.patchEntity(patchWithLocation("-23.55052", "-46.63331", null, null), user);

        assertThat(user.getLatitude()).isEqualTo(new BigDecimal("-23.55"));
        assertThat(user.getLongitude()).isEqualTo(new BigDecimal("-46.63"));
    }

    @Test
    void patchRoundsHalfUp() {
        User user = new User();

        mapper.patchEntity(patchWithLocation("-23.545", "46.625", null, null), user);

        assertThat(user.getLatitude()).isEqualTo(new BigDecimal("-23.55"));
        assertThat(user.getLongitude()).isEqualTo(new BigDecimal("46.63"));
    }

    @Test
    void putKeepsOldLocation() {
        User user = userInSaoPaulo();
        UserRequest request = new UserRequest("uid-ana", "Ana Souza", null, null, UserStatus.ACTIVE, null);

        mapper.updateEntity(request, user);

        assertThat(user.getLatitude()).isEqualByComparingTo("-23.55");
        assertThat(user.getLongitude()).isEqualByComparingTo("-46.63");
        assertThat(user.getCity()).isEqualTo("Sao Paulo");
        assertThat(user.getState()).isEqualTo("Sao Paulo");
    }

    @Test
    void responseShowsLocation() {
        UserResponse response = mapper.toResponse(userInSaoPaulo());

        assertThat(response.latitude()).isEqualByComparingTo("-23.55");
        assertThat(response.longitude()).isEqualByComparingTo("-46.63");
        assertThat(response.city()).isEqualTo("Sao Paulo");
        assertThat(response.state()).isEqualTo("Sao Paulo");
    }

    private UserPatchRequest patchWithLocation(String latitude, String longitude, String city, String state) {
        return new UserPatchRequest(null, null, null, null, null, new BigDecimal(latitude),
                new BigDecimal(longitude), city, state);
    }

    private User userInSaoPaulo() {
        User user = new User();
        user.setFirebaseUid("uid-ana");
        user.setName("Ana");
        user.setStatus(UserStatus.ACTIVE);
        user.setLatitude(new BigDecimal("-23.55"));
        user.setLongitude(new BigDecimal("-46.63"));
        user.setCity("Sao Paulo");
        user.setState("Sao Paulo");
        return user;
    }
}
