package com.venus.crud.service.jpa.fullstage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.venus.crud.dto.jpa.response.fullstage.IngredientEffectDetailResponse;
import com.venus.crud.dto.jpa.response.fullstage.IngredientFullResponse;
import com.venus.crud.entity.enums.EffectCategory;
import com.venus.crud.entity.enums.SourceType;
import com.venus.crud.entity.ingredient.Ingredient;
import com.venus.crud.entity.ingredient.IngredientCategory;
import com.venus.crud.entity.ingredient.IngredientEffect;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.mapper.jpa.ingredient.IngredientAliasMapper;
import com.venus.crud.mapper.jpa.ingredient.IngredientCategoryMapper;
import com.venus.crud.mapper.jpa.ingredient.IngredientMapper;
import com.venus.crud.mapper.jpa.ingredient.IngredientPropertyMapper;
import com.venus.crud.mapper.jpa.shared.ProfileTagMapper;
import com.venus.crud.repository.jpa.ingredient.IngredientAliasRepository;
import com.venus.crud.repository.jpa.ingredient.IngredientCategoryRepository;
import com.venus.crud.repository.jpa.ingredient.IngredientEffectRepository;
import com.venus.crud.repository.jpa.ingredient.IngredientPropertyRepository;
import com.venus.crud.repository.jpa.ingredient.IngredientRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IngredientFullServiceTest {

    private static final Long INGREDIENT_ID = 8104L;
    private static final Long CATEGORY_ID = 5L;
    private static final Long EFFECT_ID = 31L;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientCategoryRepository ingredientCategoryRepository;

    @Mock
    private IngredientAliasRepository ingredientAliasRepository;

    @Mock
    private IngredientPropertyRepository ingredientPropertyRepository;

    @Mock
    private IngredientEffectRepository ingredientEffectRepository;

    @Mock
    private IngredientMapper ingredientMapper;

    @Mock
    private IngredientCategoryMapper ingredientCategoryMapper;

    @Mock
    private IngredientAliasMapper ingredientAliasMapper;

    @Mock
    private IngredientPropertyMapper ingredientPropertyMapper;

    @Mock
    private ProfileTagMapper profileTagMapper;

    private IngredientFullService service;

    @BeforeEach
    void setUp() {
        service = new IngredientFullService(ingredientRepository, ingredientCategoryRepository,
                ingredientAliasRepository, ingredientPropertyRepository, ingredientEffectRepository, ingredientMapper,
                ingredientCategoryMapper, ingredientAliasMapper, ingredientPropertyMapper, profileTagMapper);
    }

    @Test
    void missingIngredientGivesNotFound() {
        when(ingredientRepository.findById(INGREDIENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(INGREDIENT_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void ingredientReadsAllParts() {
        IngredientCategory category = new IngredientCategory();
        category.setId(CATEGORY_ID);
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientCategory(category);

        IngredientEffect riskEffect = new IngredientEffect();
        riskEffect.setId(EFFECT_ID);
        riskEffect.setEffectCategory(EffectCategory.RISK);
        riskEffect.setEffectName("Irritacao");
        riskEffect.setSourceType(SourceType.ADMIN);
        riskEffect.setSourceReference("ANVISA-2024-0031");

        when(ingredientRepository.findById(INGREDIENT_ID)).thenReturn(Optional.of(ingredient));
        when(ingredientCategoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());
        when(ingredientAliasRepository.findByIngredientId(INGREDIENT_ID)).thenReturn(List.of());
        when(ingredientPropertyRepository.findByIngredientId(INGREDIENT_ID)).thenReturn(List.of());
        when(ingredientEffectRepository.findByIngredientId(INGREDIENT_ID)).thenReturn(List.of(riskEffect));

        IngredientFullResponse response = service.findById(INGREDIENT_ID);

        assertThat(response.category()).isNull();
        assertThat(response.aliases()).isEmpty();
        assertThat(response.properties()).isEmpty();
        assertThat(response.effects()).containsExactly(new IngredientEffectDetailResponse(EFFECT_ID, null,
                EffectCategory.RISK, "Irritacao", null, null, null, null, SourceType.ADMIN, "ANVISA-2024-0031"));
    }
}
