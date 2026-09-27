package com.venus.crud.document;

import com.venus.crud.entity.enums.IngredientDecisionAction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ScanIngredientDecision {

    private Integer position;

    private IngredientDecisionAction action;

    @Field("ingredient_id")
    private Long ingredientId;

    @Field("inci_name")
    private String inciName;
}
