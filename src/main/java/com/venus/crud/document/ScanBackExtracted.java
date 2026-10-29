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
public class ScanBackExtracted {

    @Field("ingredients_text")
    private String ingredientsText;

    private String manufacturer;

    @Field("manufacturer_address")
    private String manufacturerAddress;

    private String contact;

    private String country;

    private String batch;

    @Field("registration_number")
    private String registrationNumber;

    @Field("net_content")
    private String netContent;

    private String usage;

    private String precautions;

    private String warnings;

    private String barcode;

    private List<String> claims;

    @Field("other_text")
    private String otherText;
}
