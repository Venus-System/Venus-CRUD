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
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = {CacheConfig.class, FavoriteService.class}, properties = "spring.data.redis.port=6379")
@ImportAutoConfiguration({RedisAutoConfiguration.class, CacheAutoConfiguration.class})
@Testcontainers(disabledWithoutDocker = true)
class FavoriteServiceCacheTest {

    private static final long ANA_ID = 10L;
    private static final long BIA_ID = 11L;
    private static final long PRODUCT_ID = 15L;

    private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "createdAt");
    private static final Pageable FIRST_PAGE = PageRequest.of(0, 20, NEWEST_FIRST);
    private static final Pageable SECOND_PAGE = PageRequest.of(1, 20, NEWEST_FIRST);

    private static final String ANA_PAGE_KEY = "favoritos-do-usuario::10:0:20:createdAt: DESC";
    private static final String ANA_COUNT_KEY = "contagem-de-favoritos-do-usuario::10";

    private static final FavoriteResponse ANA_FAVORITE =
            new FavoriteResponse(1L, ANA_ID, PRODUCT_ID, OffsetDateTime.parse("2026-10-11T14:30:00-03:00"));

    @Container
    @ServiceConnection(name = "redis")
    static final GenericContainer<?> REDIS = new GenericContainer<>("redis:7.4-alpine").withExposedPorts(6379);

    @MockBean
    private FavoriteRepository favoriteRepository;

    @MockBean
    private FavoriteMapper favoriteMapper;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private StringRedisTemplate redis;

    @BeforeEach
    void setUp() {
        Set<String> oldKeys = redis.keys("*");
        redis.delete(oldKeys);

        when(favoriteRepository.findByUserId(anyLong(), any(Pageable.class)))
                .thenAnswer(call -> new SliceImpl<>(List.of(new Favorite()), call.getArgument(1), true));
        when(favoriteRepository.countByUserId(anyLong())).thenReturn(1L);
        when(favoriteMapper.toResponse(any(Favorite.class))).thenReturn(ANA_FAVORITE);
    }

    @Test
    void samePageReadsDatabaseOnce() {
        Slice<FavoriteResponse> fromDatabase = favoriteService.findByUserId(ANA_ID, FIRST_PAGE);
        Slice<FavoriteResponse> fromCache = favoriteService.findByUserId(ANA_ID, FIRST_PAGE);

        verify(favoriteRepository, times(1)).findByUserId(ANA_ID, FIRST_PAGE);
        assertThat(fromCache.getContent()).isEqualTo(fromDatabase.getContent()).containsExactly(ANA_FAVORITE);
        assertThat(fromCache.getPageable()).isEqualTo(FIRST_PAGE);
        assertThat(fromCache.hasNext()).isTrue();
        assertThat(redis.hasKey(ANA_PAGE_KEY)).isTrue();
    }

    @Test
    void eachPageHasOwnKey() {
        favoriteService.findByUserId(ANA_ID, FIRST_PAGE);
        favoriteService.findByUserId(ANA_ID, SECOND_PAGE);
        favoriteService.findByUserId(ANA_ID, FIRST_PAGE);
        favoriteService.findByUserId(ANA_ID, SECOND_PAGE);

        verify(favoriteRepository, times(1)).findByUserId(ANA_ID, FIRST_PAGE);
        verify(favoriteRepository, times(1)).findByUserId(ANA_ID, SECOND_PAGE);
    }

    @Test
    void countReadsDatabaseOnce() {
        long fromDatabase = favoriteService.countByUserId(ANA_ID);
        long fromCache = favoriteService.countByUserId(ANA_ID);

        verify(favoriteRepository, times(1)).countByUserId(ANA_ID);
        assertThat(fromCache).isEqualTo(fromDatabase).isEqualTo(1L);
        assertThat(redis.hasKey(ANA_COUNT_KEY)).isTrue();
    }

    @Test
    void createClearsPagesAndCount() {
        readPageAndCount(ANA_ID);
        mockNewFavorite(ANA_ID);

        favoriteService.create(new FavoriteRequest(ANA_ID, PRODUCT_ID));
        readPageAndCount(ANA_ID);

        verify(favoriteRepository, times(2)).findByUserId(ANA_ID, FIRST_PAGE);
        verify(favoriteRepository, times(2)).countByUserId(ANA_ID);
    }

    @Test
    void deleteClearsPagesAndCount() {
        readPageAndCount(ANA_ID);
        when(favoriteRepository.existsByUserIdAndProductId(ANA_ID, PRODUCT_ID)).thenReturn(true);

        favoriteService.delete(ANA_ID, PRODUCT_ID);
        readPageAndCount(ANA_ID);

        verify(favoriteRepository, times(2)).findByUserId(ANA_ID, FIRST_PAGE);
        verify(favoriteRepository, times(2)).countByUserId(ANA_ID);
    }

    @Test
    void otherUserCreateKeepsAnaCount() {
        readPageAndCount(ANA_ID);
        mockNewFavorite(BIA_ID);

        favoriteService.create(new FavoriteRequest(BIA_ID, PRODUCT_ID));
        readPageAndCount(ANA_ID);

        verify(favoriteRepository, times(2)).findByUserId(ANA_ID, FIRST_PAGE);
        verify(favoriteRepository, times(1)).countByUserId(ANA_ID);
    }

    private void readPageAndCount(long userId) {
        favoriteService.findByUserId(userId, FIRST_PAGE);
        favoriteService.countByUserId(userId);
    }

    private void mockNewFavorite(long userId) {
        when(favoriteRepository.existsByUserIdAndProductId(userId, PRODUCT_ID)).thenReturn(false);
        when(favoriteMapper.toEntity(any(FavoriteRequest.class))).thenReturn(new Favorite());
        when(favoriteRepository.save(any(Favorite.class))).thenAnswer(call -> call.getArgument(0));
    }
}
