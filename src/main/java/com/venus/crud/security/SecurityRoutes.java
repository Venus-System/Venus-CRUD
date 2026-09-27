package com.venus.crud.security;

import java.util.Arrays;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

public final class SecurityRoutes {

    public static final String ADMIN_LOGIN = "/api/auth/admin/login";

    public static final String[] DOCUMENTATION = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    public static final String[] CATALOG = {
            "/api/products/**",
            "/api/product-versions/**",
            "/api/product-categories/**",
            "/api/brands/**",
            "/api/claims/**",
            "/api/product-claims/**",
            "/api/product-labels/**",
            "/api/packagings/**",
            "/api/product-ingredients/**",
            "/api/ingredients/**",
            "/api/ingredient-aliases/**",
            "/api/ingredient-categories/**",
            "/api/ingredient-properties/**",
            "/api/regulations/**",
            "/api/allergies/**",
            "/api/profile-tags/**",
            "/api/media/**"
    };

    public static final String[] SCORING_CATALOG = {
            "/api/compatibility-rules/**",
            "/api/ingredient-effects/**",
            "/api/allergy-ingredients/**",
            "/api/scoring-models/**",
            "/api/score-categories/**",
            "/api/scoring-model-categories/**",
            "/api/product-scores/**"
    };

    public static final String[] PUBLIC_GET_OUTSIDE_CATALOG = {
            "/api/reviews/**",
            "/api/favorites/product/*/count",
            "/api/review-votes/review/*/count"
    };

    public static final String[] USER_DATA = {
            "/api/users/**",
            "/api/user-profiles/**",
            "/api/user-preferences/**",
            "/api/user-allergies/**",
            "/api/user-profile-tags/**",
            "/api/favorites/**",
            "/api/user-lists/**",
            "/api/user-list-items/**",
            "/api/reviews/**",
            "/api/review-votes/**",
            "/api/reports/**",
            "/api/analysis-results/**",
            "/api/personalized-scores/**",
            "/api/recommendations/**",
            "/api/rule-evaluations/**",
            "/api/scan-sessions/**",
            "/api/admin-users/**"
    };

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private SecurityRoutes() {
    }

    public static boolean isPublic(HttpMethod method, String path) {
        if (matchesAny(DOCUMENTATION, path)) {
            return true;
        }
        if (HttpMethod.POST.equals(method)) {
            return PATH_MATCHER.match(ADMIN_LOGIN, path);
        }
        return HttpMethod.GET.equals(method)
                && (matchesAny(CATALOG, path) || matchesAny(SCORING_CATALOG, path)
                        || matchesAny(PUBLIC_GET_OUTSIDE_CATALOG, path));
    }

    private static boolean matchesAny(String[] patterns, String path) {
        return Arrays.stream(patterns).anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }
}
