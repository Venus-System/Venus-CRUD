package com.venus.crud.service.jpa.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.venus.crud.config.CacheConfig;
import com.venus.crud.dto.jpa.request.user.FavoriteRequest;
import com.venus.crud.dto.jpa.response.user.FavoriteResponse;
import com.venus.crud.entity.user.Favorite;
import com.venus.crud.mapper.jpa.user.FavoriteMapper;
import com.venus.crud.repository.jpa.user.FavoriteRepository;
import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = {CacheConfig.class, FavoriteService.class, FavoriteServiceCommitTest.TransactionConfig.class},
        properties = "spring.data.redis.port=6379")
@ImportAutoConfiguration({RedisAutoConfiguration.class, CacheAutoConfiguration.class})
@Testcontainers(disabledWithoutDocker = true)
class FavoriteServiceCommitTest {

    private static final long ANA_ID = 10L;
    private static final long PRODUCT_ID = 15L;
    private static final String ANA_COUNT_KEY = "contagem-de-favoritos-do-usuario::10";

    private static final FavoriteResponse ANA_FAVORITE =
            new FavoriteResponse(1L, ANA_ID, PRODUCT_ID, OffsetDateTime.parse("2026-10-11T14:30:00-03:00"));

    @Container
    @ServiceConnection(name = "redis")
    static final GenericContainer<?> REDIS = new GenericContainer<>("redis:7.4-alpine").withExposedPorts(6379);

    @TestConfiguration
    @EnableTransactionManagement(order = Ordered.HIGHEST_PRECEDENCE + 1)
    static class TransactionConfig {

        @Bean
        PlatformTransactionManager transactionManager() {
            return mock(PlatformTransactionManager.class);
        }
    }

    @MockBean
    private FavoriteRepository favoriteRepository;

    @MockBean
    private FavoriteMapper favoriteMapper;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private StringRedisTemplate redis;

    @BeforeEach
    void setUp() {
        redis.delete(redis.keys("*"));

        when(transactionManager.getTransaction(any())).thenAnswer(call -> new SimpleTransactionStatus());
        when(favoriteRepository.countByUserId(anyLong())).thenReturn(1L);
        when(favoriteRepository.existsByUserIdAndProductId(ANA_ID, PRODUCT_ID)).thenReturn(false);
        when(favoriteMapper.toEntity(any(FavoriteRequest.class))).thenReturn(new Favorite());
        when(favoriteRepository.save(any(Favorite.class))).thenAnswer(call -> call.getArgument(0));
        when(favoriteMapper.toResponse(any(Favorite.class))).thenReturn(ANA_FAVORITE);
    }

    @Test
    void createClearsCacheAfterCommit() {
        favoriteService.countByUserId(ANA_ID);
        AtomicBoolean keyAtCommit = new AtomicBoolean(false);
        doAnswer(call -> {
            keyAtCommit.set(redis.hasKey(ANA_COUNT_KEY));
            return null;
        }).when(transactionManager).commit(any());

        favoriteService.create(new FavoriteRequest(ANA_ID, PRODUCT_ID));

        assertThat(keyAtCommit).isTrue();
        assertThat(redis.hasKey(ANA_COUNT_KEY)).isFalse();
    }
}
