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
public class ScanSnapshotProduct {

    @Field("product_id")
    private Long productId;

    private String name;

    @Field("brand_id")
    private Long brandId;

    @Field("product_category_id")
    private Long productCategoryId;
}
