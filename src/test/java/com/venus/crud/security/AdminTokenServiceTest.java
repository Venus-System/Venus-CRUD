package com.venus.crud.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.venus.crud.config.SecurityProperties;
import com.venus.crud.entity.admin.AdminUser;
import com.venus.crud.entity.enums.AdminRole;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

class AdminTokenServiceTest {

    private static final String SECRET = "segredo-de-teste-com-mais-de-32-bytes!!";
    private static final String OTHER_SECRET = "outro-segredo-de-teste-com-mais-de-32-bytes";

    @Test
    void issuedTokenComesBackWithIdAndRole() {
        AdminTokenService service = service(SECRET, Duration.ofHours(8));

        AdminTokenService.AdminToken token = service.issue(admin(3L, AdminRole.MODERATOR));
        Jwt jwt = service.decoder().decode(token.value());
        AbstractAuthenticationToken authentication = new AdminJwtAuthenticationConverter().convert(jwt);

        assertThat(jwt.getSubject()).isEqualTo("3");
        assertThat(jwt.getClaimAsString(JwtClaimNames.ISS)).isEqualTo(AdminTokenService.ISSUER);
        assertThat(authentication.getName()).isEqualTo("3");
        assertThat(authentication.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder(SecurityRoles.MODERATOR, SecurityRoles.ANALYST);
        assertThat(token.expiresAt()).isAfter(Instant.now().plus(Duration.ofHours(7)));
    }

    @Test
    void expiredTokenIsRejected() {
        Instant twoHoursAgo = Instant.now().minus(Duration.ofHours(2));
        String token = sign(SECRET, JwtClaimsSet.builder()
                .issuer(AdminTokenService.ISSUER)
                .subject("3")
                .issuedAt(twoHoursAgo)
                .expiresAt(twoHoursAgo.plus(Duration.ofHours(1)))
                .claim(AdminTokenService.ROLE_CLAIM, AdminRole.ADMIN.name())
                .build());

        assertThatThrownBy(() -> service(SECRET, Duration.ofHours(8)).decoder().decode(token))
                .isInstanceOf(JwtValidationException.class);
    }

    @Test
    void tokenSignedWithAnotherSecretIsRejected() {
        String token = service(OTHER_SECRET, Duration.ofHours(8)).issue(admin(3L, AdminRole.ADMIN)).value();

        assertThatThrownBy(() -> service(SECRET, Duration.ofHours(8)).decoder().decode(token))
                .isInstanceOf(BadJwtException.class);
    }

    @Test
    void unknownRoleGetsNoAuthority() {
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "HS256").subject("3")
                .claim(AdminTokenService.ROLE_CLAIM, "SUPERUSER").build();

        assertThat(new AdminJwtAuthenticationConverter().convert(jwt).getAuthorities()).isEmpty();
    }

    @Test
    void shortSecretIsRefused() {
        assertThatThrownBy(() -> new SecurityProperties.AdminToken("curto", Duration.ofHours(8)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("32 bytes");
    }

    private String sign(String secret, JwtClaimsSet claims) {
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return new NimbusJwtEncoder(new ImmutableSecret<>(key))
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }

    private AdminTokenService service(String secret, Duration expiration) {
        return new AdminTokenService(new SecurityProperties(
                new SecurityProperties.AdminToken(secret, expiration),
                new SecurityProperties.Firebase("venus-81bac")));
    }

    private AdminUser admin(Long id, AdminRole role) {
        AdminUser admin = new AdminUser();
        admin.setId(id);
        admin.setEmail("ana@venus.com");
        admin.setRole(role);
        return admin;
    }
}
