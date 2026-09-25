package com.venus.crud.document;

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
public class ScanFrontExtracted {

    private String brand;

    @Field("product_name")
    private String productName;

    private String presentation;

    private String capacity;

    private String category;

    private List<String> claims;
}
