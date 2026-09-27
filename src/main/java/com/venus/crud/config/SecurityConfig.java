package com.venus.crud.config;

import com.venus.crud.security.AdminJwtAuthenticationConverter;
import com.venus.crud.security.AdminTokenService;
import com.venus.crud.security.FirebaseJwtAuthenticationConverter;
import com.venus.crud.security.FirebaseTokenValidator;
import com.venus.crud.security.SecurityErrorHandler;
import com.venus.crud.security.SecurityRoutes;
import jakarta.servlet.DispatcherType;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String MODERATOR = "MODERATOR";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            JwtIssuerAuthenticationManagerResolver authenticationManagerResolver,
            SecurityErrorHandler securityErrorHandler) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(SecurityRoutes.DOCUMENTATION).permitAll()
                        .requestMatchers(HttpMethod.POST, SecurityRoutes.ADMIN_LOGIN).permitAll()
                        .requestMatchers(HttpMethod.GET, SecurityRoutes.CATALOG).permitAll()
                        .requestMatchers(HttpMethod.GET, SecurityRoutes.SCORING_CATALOG).permitAll()
                        .requestMatchers(HttpMethod.GET, SecurityRoutes.PUBLIC_GET_OUTSIDE_CATALOG).permitAll()
                        .requestMatchers(SecurityRoutes.CATALOG).hasRole(MODERATOR)
                        .requestMatchers(SecurityRoutes.SCORING_CATALOG).hasRole(ADMIN)
                        .requestMatchers(SecurityRoutes.USER_DATA).authenticated()
                        .anyRequest().hasRole(ADMIN))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationManagerResolver(authenticationManagerResolver)
                        .authenticationEntryPoint(securityErrorHandler)
                        .accessDeniedHandler(securityErrorHandler))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(securityErrorHandler)
                        .accessDeniedHandler(securityErrorHandler));

        return http.build();
    }

    @Bean
    public JwtIssuerAuthenticationManagerResolver authenticationManagerResolver(SecurityProperties securityProperties,
            AdminTokenService adminTokenService) {
        String projectId = securityProperties.firebase().projectId();
        Map<String, AuthenticationManager> managers = Map.of(
                FirebaseTokenValidator.issuerFor(projectId),
                authenticationManager(FirebaseTokenValidator.decoderFor(projectId), new FirebaseJwtAuthenticationConverter()),
                AdminTokenService.ISSUER,
                authenticationManager(adminTokenService.decoder(), new AdminJwtAuthenticationConverter()));
        return new JwtIssuerAuthenticationManagerResolver(managers::get);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private AuthenticationManager authenticationManager(JwtDecoder decoder,
            Converter<Jwt, ? extends AbstractAuthenticationToken> converter) {
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(decoder);
        provider.setJwtAuthenticationConverter(converter);
        return new ProviderManager(provider);
    }
}
