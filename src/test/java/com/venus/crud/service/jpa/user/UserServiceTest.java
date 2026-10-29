package com.venus.crud.service.jpa.user;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.venus.crud.entity.user.User;
import com.venus.crud.exception.InvalidStateTransitionException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.mapper.jpa.user.UserMapper;
import com.venus.crud.repository.jpa.user.UserRepository;
import com.venus.crud.security.CurrentUserProvider;
import java.sql.SQLException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.UncategorizedSQLException;
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

    @Test
    void setActiveCallsProcedure() {
        when(userRepository.findById(7L)).thenReturn(Optional.of(userWithId(7L)));

        service.setActive(7L, false);

        verify(userRepository).setActive(7L, false);
    }

    @Test
    void unknownUserSkipsProcedure() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setActive(99L, false))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, never()).setActive(anyLong(), anyBoolean());
    }

    @Test
    void blockedUserGivesConflict() {
        when(userRepository.findById(8L)).thenReturn(Optional.of(userWithId(8L)));
        SQLException blockedError = new SQLException("usuario bloqueado", "VE001");
        doThrow(new UncategorizedSQLException("setActive", "CALL", blockedError))
                .when(userRepository).setActive(8L, true);

        assertThatThrownBy(() -> service.setActive(8L, true))
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    private static User userWithId(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}
