package com.venus.crud.service.jpa.ingredient;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.venus.crud.entity.ingredient.IngredientCategory;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.mapper.jpa.ingredient.IngredientMapper;
import com.venus.crud.repository.jpa.ingredient.IngredientCategoryRepository;
import com.venus.crud.repository.jpa.ingredient.IngredientRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    private static final Pageable FIRST_PAGE = PageRequest.of(0, 20);

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientCategoryRepository ingredientCategoryRepository;

    @Mock
    private IngredientMapper ingredientMapper;

    private IngredientService service;

    @BeforeEach
    void setUp() {
        service = new IngredientService(ingredientRepository, ingredientCategoryRepository, ingredientMapper);
    }

    @Test
    void searchWithUnknownCategoryNameFailsWithNotFound() {
        when(ingredientCategoryRepository.findByNameIgnoreCase("Nao Existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.search(null, null, "Nao Existe", null, FIRST_PAGE))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchWithCategoryNameSendsTheCategoryIdAsTree() {
        IngredientCategory surfactantCategory = new IngredientCategory();
        surfactantCategory.setId(5L);
        when(ingredientCategoryRepository.findByNameIgnoreCase("Tensoativo")).thenReturn(Optional.of(surfactantCategory));
        when(ingredientRepository.search(any(), any(), any(), any(), any())).thenReturn(new SliceImpl<>(List.of()));

        service.search("  ", null, "Tensoativo", null, FIRST_PAGE);

        verify(ingredientRepository).search(null, null, 5L, null, FIRST_PAGE);
    }
}
