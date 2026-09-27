package com.venus.crud.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;

class FirebaseTokenValidatorTest {

    private static final String PROJECT_ID = "venus-81bac";

    private final OAuth2TokenValidator<Jwt> validator = FirebaseTokenValidator.forProject(PROJECT_ID);

    @Test
    void acceptsTokenOfTheProject() {
        assertThat(validator.validate(token().build()).hasErrors()).isFalse();
    }

    @Test
    void rejectsTokenOfAnotherProject() {
        assertThat(validator.validate(token().audience(List.of("outro-projeto")).build()).hasErrors()).isTrue();
    }

    @Test
    void rejectsTokenFromAnotherIssuer() {
        Jwt jwt = token().issuer("https://securetoken.google.com/outro-projeto").build();
        assertThat(validator.validate(jwt).hasErrors()).isTrue();
    }

    @Test
    void rejectsTokenWithoutSubject() {
        assertThat(validator.validate(token().subject(" ").build()).hasErrors()).isTrue();
    }

    @Test
    void rejectsExpiredToken() {
        Instant now = Instant.now();
        Jwt jwt = token().issuedAt(now.minusSeconds(7200)).expiresAt(now.minusSeconds(3600)).build();
        assertThat(validator.validate(jwt).hasErrors()).isTrue();
    }

    private Jwt.Builder token() {
        Instant now = Instant.now();
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuer(FirebaseTokenValidator.issuerFor(PROJECT_ID))
                .audience(List.of(PROJECT_ID))
                .subject("uid-ana")
                .issuedAt(now.minusSeconds(60))
                .expiresAt(now.plusSeconds(3600));
    }
}
