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
public class ScanDevice {

    @Field("device_id")
    private String deviceId;

    private String platform;

    @Field("app_version")
    private String appVersion;

    private String model;
}
