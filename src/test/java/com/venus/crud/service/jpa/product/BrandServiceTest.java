package com.venus.crud.service.jpa.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.venus.crud.mapper.jpa.product.BrandMapper;
import com.venus.crud.repository.jpa.product.BrandRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    private static final Pageable FIRST_PAGE = PageRequest.of(0, 20);

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private BrandMapper brandMapper;

    private BrandService service;

    @BeforeEach
    void setUp() {
        service = new BrandService(brandRepository, brandMapper);
    }

    @Test
    void searchTreatsBlankTextAsNoFilter() {
        when(brandRepository.search(any(), any(), any(), any(), any(), any())).thenReturn(new SliceImpl<>(List.of()));

        service.search("   ", "", null, null, true, FIRST_PAGE);

        verify(brandRepository).search(null, null, null, null, true, FIRST_PAGE);
    }
}
