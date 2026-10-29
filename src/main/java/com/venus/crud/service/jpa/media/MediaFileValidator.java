package com.venus.crud.service.jpa.media;

import com.venus.crud.config.MediaProperties;
import com.venus.crud.entity.enums.MediaPurpose;
import com.venus.crud.exception.InvalidFileException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaFileValidator {

    private final MediaProperties mediaProperties;

    public MediaFileValidator(MediaProperties mediaProperties) {
        this.mediaProperties = mediaProperties;
    }

    public void validateFile(MultipartFile file, MediaPurpose purpose) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("O arquivo de imagem e obrigatorio.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !mediaProperties.allowedImageTypes().contains(contentType)) {
            throw new InvalidFileException("Tipo de arquivo nao suportado. Tipos aceitos: "
                    + String.join(", ", mediaProperties.allowedImageTypes()) + ".");
        }

        MediaProperties.Limits limits = mediaProperties.limitsFor(purpose);
        if (file.getSize() > limits.maxBytes()) {
            throw new InvalidFileException("A imagem excede o tamanho maximo de " + limits.maxBytes() + " bytes.");
        }
    }

    public void validateDimensions(CloudinaryUpload upload, MediaPurpose purpose) {
        MediaProperties.Limits limits = mediaProperties.limitsFor(purpose);
        boolean tooWide = upload.width() != null && upload.width() > limits.maxWidth();
        boolean tooTall = upload.height() != null && upload.height() > limits.maxHeight();

        if (tooWide || tooTall) {
            throw new InvalidFileException("A imagem excede as dimensoes maximas de "
                    + limits.maxWidth() + "x" + limits.maxHeight() + " pixels.");
        }
    }
}
