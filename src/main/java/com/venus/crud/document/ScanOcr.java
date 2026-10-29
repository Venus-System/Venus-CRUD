package com.venus.crud.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ScanOcr {

    private ScanOcrFront front;

    private ScanOcrBack back;
}
