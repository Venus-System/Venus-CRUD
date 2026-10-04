package com.venus.crud.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class PreAuthorizeCoverageTest {

    private static final String CONTROLLER_PACKAGE = "com.venus.crud.controller";
    private static final int USER_DATA_CONTROLLERS = 23;
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Test
    void everyHandlerOnUserDataRoutesHasPreAuthorize() throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));

        List<String> missing = new ArrayList<>();
        int checkedControllers = 0;
        for (BeanDefinition definition : scanner.findCandidateComponents(CONTROLLER_PACKAGE)) {
            Class<?> controller = Class.forName(definition.getBeanClassName());
            if (!isUserDataController(controller)) {
                continue;
            }
            checkedControllers++;
            for (Method method : controller.getDeclaredMethods()) {
                if (AnnotatedElementUtils.hasAnnotation(method, RequestMapping.class)
                        && !AnnotatedElementUtils.hasAnnotation(method, PreAuthorize.class)) {
                    missing.add(controller.getSimpleName() + "." + method.getName());
                }
            }
        }

        assertThat(checkedControllers).isEqualTo(USER_DATA_CONTROLLERS);
        assertThat(missing).as("metodos sem @PreAuthorize").isEmpty();
    }

    private boolean isUserDataController(Class<?> controller) {
        RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(controller, RequestMapping.class);
        if (mapping == null || mapping.path().length == 0) {
            return false;
        }
        String basePath = mapping.path()[0];
        return Arrays.stream(SecurityRoutes.USER_DATA).anyMatch(pattern -> PATH_MATCHER.match(pattern, basePath));
    }
}
