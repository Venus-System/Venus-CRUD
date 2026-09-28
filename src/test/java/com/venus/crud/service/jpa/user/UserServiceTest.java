package com.venus.crud.service.jpa.user;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.venus.crud.entity.user.User;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.mapper.jpa.user.UserMapper;
import com.venus.crud.repository.jpa.user.UserRepository;
import com.venus.crud.security.CurrentUserProvider;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CurrentUserProvider currentUserProvider;

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(userRepository, userMapper, passwordEncoder, currentUserProvider);
    }

    @Test
    void registerAccessCallsTheFunctionWithTheLoggedUserId() {
        User user = new User();
        user.setId(7L);
        when(currentUserProvider.activeUser()).thenReturn(Optional.of(user));

        service.registerAccess();

        verify(userRepository).registerAccess(7L, "APP_OPEN", "{}");
    }

    @Test
    void registerAccessFailsWithoutALoggedUser() {
        when(currentUserProvider.activeUser()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.registerAccess())
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
