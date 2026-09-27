package com.venus.crud.service.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.venus.crud.document.ScanSession;
import com.venus.crud.document.ScanSource;
import com.venus.crud.dto.mongo.response.ScanSessionResponse;
import com.venus.crud.entity.user.User;
import com.venus.crud.mapper.mongo.ScanSessionMapperImpl;
import com.venus.crud.repository.jpa.user.UserRepository;
import com.venus.crud.repository.mongo.ScanSessionRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class ScanSessionServiceTest {

    private final Pageable pageable = PageRequest.of(0, 20);

    @Mock
    private ScanSessionRepository scanSessionRepository;

    @Mock
    private ScanSessionRequestValidator scanSessionRequestValidator;

    @Mock
    private IngredientMatcher ingredientMatcher;

    @Mock
    private ScanCloudinaryService scanCloudinaryService;

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<Iterable<Long>> userIds;

    private ScanSessionService service;

    @BeforeEach
    void setUp() {
        service = new ScanSessionService(scanSessionRepository, new ScanSessionMapperImpl(), scanSessionRequestValidator,
                ingredientMatcher, scanCloudinaryService, userRepository);
    }

    @Test
    void listingBringsTheSenderNameWithASingleQuery() {
        when(scanSessionRepository.findAllBy(pageable))
                .thenReturn(new SliceImpl<>(List.of(scanFrom(10L), scanFrom(10L), scanFrom(11L)), pageable, false));
        when(userRepository.findAllById(any())).thenReturn(List.of(user(10L, "Ana Souza"), user(11L, "Bia Lima")));

        Slice<ScanSessionResponse> page = service.findAll(pageable);

        assertThat(page.getContent()).extracting(response -> response.source().userName())
                .containsExactly("Ana Souza", "Ana Souza", "Bia Lima");
        verify(userRepository, times(1)).findAllById(userIds.capture());
        assertThat(userIds.getValue()).containsExactlyInAnyOrder(10L, 11L);
    }

    @Test
    void deletedUserAndScanWithoutSourceGiveNoName() {
        when(scanSessionRepository.findAllBy(pageable))
                .thenReturn(new SliceImpl<>(List.of(scanFrom(99L), new ScanSession()), pageable, false));
        when(userRepository.findAllById(any())).thenReturn(List.of());

        Slice<ScanSessionResponse> page = service.findAll(pageable);

        assertThat(page.getContent().get(0).source().userName()).isNull();
        assertThat(page.getContent().get(1).source()).isNull();
    }

    @Test
    void pageWithoutSendersDoesNotQueryUsers() {
        when(scanSessionRepository.findAllBy(pageable))
                .thenReturn(new SliceImpl<>(List.of(new ScanSession()), pageable, false));

        service.findAll(pageable);

        verifyNoInteractions(userRepository);
    }

    private ScanSession scanFrom(Long userId) {
        ScanSource source = new ScanSource();
        source.setUserId(userId);
        ScanSession scanSession = new ScanSession();
        scanSession.setSource(source);
        return scanSession;
    }

    private User user(Long id, String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        return user;
    }
}
