package com.venus.crud.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.venus.crud.config.SecurityProperties;
import com.venus.crud.entity.admin.AdminUser;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminTokenService {

    public static final String ISSUER = "venus-crud";
    public static final String ROLE_CLAIM = "role";

    private static final String EMAIL_CLAIM = "email";
    private static final String KEY_ALGORITHM = "HmacSHA256";

    private final SecretKey secretKey;
    private final Duration expiration;
    private final JwtEncoder encoder;

    public AdminTokenService(SecurityProperties securityProperties) {
        this.secretKey = new SecretKeySpec(
                securityProperties.adminToken().secret().getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
        this.expiration = securityProperties.adminToken().expiration();
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    public AdminToken issue(AdminUser adminUser) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(expiration);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .subject(String.valueOf(adminUser.getId()))
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim(ROLE_CLAIM, adminUser.getRole().name())
                .claim(EMAIL_CLAIM, adminUser.getEmail())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String value = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new AdminToken(value, expiresAt);
    }

    public JwtDecoder decoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(ISSUER));
        return decoder;
    }

    public record AdminToken(String value, Instant expiresAt) {
    }
}
