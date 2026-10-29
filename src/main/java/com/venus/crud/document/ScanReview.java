package com.venus.crud.document;

import java.time.OffsetDateTime;
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
public class ScanReview {

    @Field("decided_by_admin_id")
    private Long decidedByAdminId;

    @Field("decided_at")
    private OffsetDateTime decidedAt;

    private String reason;

    @Field("ingredient_decisions")
    private List<ScanIngredientDecision> ingredientDecisions;
}
