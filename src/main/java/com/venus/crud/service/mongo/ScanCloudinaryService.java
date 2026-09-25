package com.venus.crud.service.mongo;

import com.cloudinary.Cloudinary;
import com.venus.crud.config.CloudinaryProperties;
import com.venus.crud.config.MediaProperties;
import com.venus.crud.dto.mongo.response.ScanUploadSignatureResponse;
import com.venus.crud.dto.mongo.response.ScanUploadSignaturesResponse;
import com.venus.crud.entity.enums.MediaDeliveryType;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ScanCloudinaryService {

    public static final String FRONT = "front";
    public static final String BACK = "back";

    private final Cloudinary productsCloudinary;
    private final CloudinaryProperties cloudinaryProperties;
    private final MediaProperties mediaProperties;

    public ScanCloudinaryService(@Qualifier("productsCloudinary") Cloudinary productsCloudinary,
            CloudinaryProperties cloudinaryProperties, MediaProperties mediaProperties) {
        this.productsCloudinary = productsCloudinary;
        this.cloudinaryProperties = cloudinaryProperties;
        this.mediaProperties = mediaProperties;
    }

    public static String publicIdFor(UUID scanId, String side) {
        return "scans/" + scanId + "/" + side;
    }

    public ScanUploadSignaturesResponse signUploads(UUID scanId) {
        long timestamp = Instant.now().getEpochSecond();
        CloudinaryProperties.Account account = cloudinaryProperties.products();
        return new ScanUploadSignaturesResponse(
                cloudinaryProperties.cloudName(),
                account.apiKey(),
                timestamp,
                account.uploadPreset(),
                deliveryType(),
                true,
                sign(publicIdFor(scanId, FRONT), timestamp, account),
                sign(publicIdFor(scanId, BACK), timestamp, account));
    }

    public String secureUrlFor(String publicId) {
        return productsCloudinary.url()
                .secure(true)
                .type(deliveryType())
                .signed(mediaProperties.defaultDeliveryType() == MediaDeliveryType.AUTHENTICATED)
                .generate(publicId);
    }

    private ScanUploadSignatureResponse sign(String publicId, long timestamp, CloudinaryProperties.Account account) {
        Map<String, Object> params = new HashMap<>();
        params.put("public_id", publicId);
        params.put("timestamp", timestamp);
        params.put("type", deliveryType());
        params.put("overwrite", true);
        if (StringUtils.hasText(account.uploadPreset())) {
            params.put("upload_preset", account.uploadPreset());
        }
        return new ScanUploadSignatureResponse(publicId,
                productsCloudinary.apiSignRequest(params, account.apiSecret(), productsCloudinary.config.signatureVersion));
    }

    private String deliveryType() {
        return mediaProperties.defaultDeliveryType().name().toLowerCase(Locale.ROOT);
    }
}
