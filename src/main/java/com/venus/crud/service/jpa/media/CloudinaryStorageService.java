package com.venus.crud.service.jpa.media;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.venus.crud.config.CloudinaryProperties;
import com.venus.crud.config.MediaProperties;
import com.venus.crud.entity.enums.MediaPurpose;
import com.venus.crud.exception.ServiceUnavailableException;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryStorageService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryStorageService.class);

    private final Cloudinary usersCloudinary;
    private final Cloudinary productsCloudinary;
    private final CloudinaryProperties cloudinaryProperties;
    private final MediaProperties mediaProperties;

    public CloudinaryStorageService(@Qualifier("usersCloudinary") Cloudinary usersCloudinary,
            @Qualifier("productsCloudinary") Cloudinary productsCloudinary,
            CloudinaryProperties cloudinaryProperties, MediaProperties mediaProperties) {
        this.usersCloudinary = usersCloudinary;
        this.productsCloudinary = productsCloudinary;
        this.cloudinaryProperties = cloudinaryProperties;
        this.mediaProperties = mediaProperties;
    }

    public CloudinaryUpload upload(MultipartFile file, MediaPurpose purpose, String publicId) {
        try {
            Map<String, Object> result = clientFor(purpose).uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "public_id", publicId,
                    "upload_preset", cloudinaryProperties.accountFor(purpose).uploadPreset(),
                    "resource_type", mediaProperties.defaultResourceType(),
                    "type", deliveryType(),
                    "overwrite", false
            ));
            return toUpload(result);
        } catch (IOException | RuntimeException ex) {
            log.error("Falha ao enviar a imagem {} para o Cloudinary", publicId, ex);
            throw new ServiceUnavailableException("Falha ao enviar a imagem para o servico de midia. Tente novamente mais tarde.", ex);
        }
    }

    public void destroy(String publicId, MediaPurpose purpose) {
        try {
            clientFor(purpose).uploader().destroy(publicId, ObjectUtils.asMap(
                    "resource_type", mediaProperties.defaultResourceType(),
                    "type", deliveryType(),
                    "invalidate", true
            ));
        } catch (IOException | RuntimeException ex) {
            log.error("Falha ao remover a imagem {} do Cloudinary", publicId, ex);
            throw new ServiceUnavailableException("Falha ao remover a imagem do servico de midia. Tente novamente mais tarde.", ex);
        }
    }

    private Cloudinary clientFor(MediaPurpose purpose) {
        return purpose == MediaPurpose.AVATAR ? usersCloudinary : productsCloudinary;
    }

    private String deliveryType() {
        return mediaProperties.defaultDeliveryType().name().toLowerCase(Locale.ROOT);
    }

    private CloudinaryUpload toUpload(Map<String, Object> result) {
        return new CloudinaryUpload(
                asString(result.get("public_id")),
                asString(result.get("asset_id")),
                asLong(result.get("version")),
                asString(result.get("secure_url")),
                asString(result.get("format")),
                asInteger(result.get("width")),
                asInteger(result.get("height")),
                asLong(result.get("bytes")),
                asString(result.get("folder"))
        );
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private Long asLong(Object value) {
        return value instanceof Number number ? number.longValue() : null;
    }

    private Integer asInteger(Object value) {
        return value instanceof Number number ? number.intValue() : null;
    }
}
