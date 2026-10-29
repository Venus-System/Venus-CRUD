package com.venus.crud.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({CloudinaryProperties.class, MediaProperties.class})
public class CloudinaryConfig {

    @Bean
    public Cloudinary usersCloudinary(CloudinaryProperties properties) {
        return buildClient(properties.cloudName(), properties.users());
    }

    @Bean
    public Cloudinary productsCloudinary(CloudinaryProperties properties) {
        return buildClient(properties.cloudName(), properties.products());
    }

    private Cloudinary buildClient(String cloudName, CloudinaryProperties.Account account) {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", account.apiKey(),
                "api_secret", account.apiSecret(),
                "secure", true
        ));
    }
}
