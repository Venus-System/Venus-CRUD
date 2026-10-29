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
public class ScanOcrBack {

    @Field("full_text")
    private String fullText;

    private List<String> lines;

    private ScanBackExtracted extracted;
}
