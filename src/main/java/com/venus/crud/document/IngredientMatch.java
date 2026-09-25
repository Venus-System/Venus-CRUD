package com.venus.crud.document;

import com.venus.crud.entity.enums.IngredientMatchStatus;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class IngredientMatch {

    private IngredientMatchStatus status;

    @Field("ingredient_id")
    private Long ingredientId;

    @Field("matched_name")
    private String matchedName;

    private List<IngredientCandidate> candidates;
}
