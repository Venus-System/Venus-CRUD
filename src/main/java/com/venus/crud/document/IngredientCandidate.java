package com.venus.crud.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class IngredientCandidate {

    @Field("ingredient_id")
    private Long ingredientId;

    @Field("inci_name")
    private String inciName;
}
