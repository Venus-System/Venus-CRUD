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
public class ScanImage {

    @Field("public_id")
    private String publicId;

    @Field("secure_url")
    private String secureUrl;

    private Integer width;

    private Integer height;

    private String format;

    private Long bytes;
}
