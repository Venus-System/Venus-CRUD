package com.venus.crud.service.jpa.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.venus.crud.config.CacheConfig;
import com.venus.crud.dto.jpa.request.user.FavoriteRequest;
import com.venus.crud.dto.jpa.response.user.FavoriteResponse;
import com.venus.crud.entity.user.Favorite;
import com.venus.crud.mapper.jpa.user.FavoriteMapper;
import com.venus.crud.repository.jpa.user.FavoriteRepository;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@SpringBootTest(classes = {CacheConfig.class, FavoriteService.class}, properties = {
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=1"
})
@ImportAutoConfiguration({RedisAutoConfiguration.class, CacheAutoConfiguration.class})
class FavoriteServiceRedisDownTest {

    private static final long ANA_ID = 10L;
    private static final long PRODUCT_ID = 15L;
    private static final Pageable FIRST_PAGE = PageRequest.of(0, 20);

    private static final FavoriteResponse ANA_FAVORITE =
            new FavoriteResponse(1L, ANA_ID, PRODUCT_ID, OffsetDateTime.parse("2026-10-11T14:30:00-03:00"));

    @MockBean
    private FavoriteRepository favoriteRepository;

    @MockBean
    private FavoriteMapper favoriteMapper;

    @Autowired
    private FavoriteService favoriteService;

    @BeforeEach
    void setUp() {
        when(favoriteRepository.findByUserId(anyLong(), any(Pageable.class)))
                .thenAnswer(call -> new SliceImpl<>(List.of(new Favorite()), call.getArgument(1), false));
        when(favoriteRepository.countByUserId(anyLong())).thenReturn(1L);
        when(favoriteMapper.toResponse(any(Favorite.class))).thenReturn(ANA_FAVORITE);
    }

    @Test
    void readsFromDatabase() {
        favoriteService.findByUserId(ANA_ID, FIRST_PAGE);
        Slice<FavoriteResponse> page = favoriteService.findByUserId(ANA_ID, FIRST_PAGE);
        long count = favoriteService.countByUserId(ANA_ID);

        verify(favoriteRepository, times(2)).findByUserId(ANA_ID, FIRST_PAGE);
        assertThat(page.getContent()).containsExactly(ANA_FAVORITE);
        assertThat(count).isEqualTo(1L);
    }

    @Test
    void createAndDeleteWork() {
        when(favoriteRepository.existsByUserIdAndProductId(ANA_ID, PRODUCT_ID)).thenReturn(false, true);
        when(favoriteMapper.toEntity(any(FavoriteRequest.class))).thenReturn(new Favorite());
        when(favoriteRepository.save(any(Favorite.class))).thenAnswer(call -> call.getArgument(0));

        FavoriteResponse created = favoriteService.create(new FavoriteRequest(ANA_ID, PRODUCT_ID));
        favoriteService.delete(ANA_ID, PRODUCT_ID);

        assertThat(created).isEqualTo(ANA_FAVORITE);
        verify(favoriteRepository).deleteByUserIdAndProductId(ANA_ID, PRODUCT_ID);
    }
}
