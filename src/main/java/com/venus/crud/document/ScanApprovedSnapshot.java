package com.venus.crud.document;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ScanApprovedSnapshot {

    private ScanSnapshotProduct product;

    private List<ScanIngredientDecision> ingredients;
}
