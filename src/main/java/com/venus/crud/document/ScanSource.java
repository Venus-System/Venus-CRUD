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
public class ScanSource {

    @Field("user_id")
    private Long userId;

    @Field("firebase_uid")
    private String firebaseUid;
}
