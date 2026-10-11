package com.venus.crud.service.jpa.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.venus.crud.mapper.jpa.product.ProductMapper;
import com.venus.crud.repository.jpa.product.ProductRepository;
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
class ProductServiceTest {

    private static final Pageable FIRST_PAGE = PageRequest.of(0, 20);

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    private ProductService service;

    @BeforeEach
    void setUp() {
        service = new ProductService(productRepository, productMapper);
    }

    @Test
    void searchTreatsBlankTextAsNoFilter() {
        when(productRepository.search(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new SliceImpl<>(List.of()));

        service.search("   ", null, null, null, "", null, true, null, FIRST_PAGE);

        verify(productRepository).search(null, null, null, null, null, null, true, null, FIRST_PAGE);
    }
}
