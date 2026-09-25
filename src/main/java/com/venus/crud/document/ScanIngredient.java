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
public class ScanIngredient {

    private Integer position;

    @Field("raw_name")
    private String rawName;

    @Field("normalized_name")
    private String normalizedName;

    private IngredientMatch match;
}
