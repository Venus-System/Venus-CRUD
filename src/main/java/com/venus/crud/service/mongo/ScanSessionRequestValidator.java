package com.venus.crud.service.mongo;

import com.venus.crud.dto.mongo.request.ScanImageRequest;
import com.venus.crud.dto.mongo.request.ScanIngredientRequest;
import com.venus.crud.dto.mongo.request.ScanSessionRequest;
import com.venus.crud.exception.InvalidRequestException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ScanSessionRequestValidator {

    public void validate(ScanSessionRequest request) {
        List<String> violations = new ArrayList<>();
        checkPeriod(request, violations);
        checkOcr(request, violations);
        checkPositions(request.ingredients(), violations);
        checkImages(request, violations);
        if (!violations.isEmpty()) {
            throw new InvalidRequestException("Dados do scan invalidos", violations);
        }
    }

    private void checkPeriod(ScanSessionRequest request, List<String> violations) {
        if (request.finishedAt().isBefore(request.startedAt())) {
            violations.add("finishedAt: tem que ser igual ou depois de startedAt");
        }
    }

    private void checkOcr(ScanSessionRequest request, List<String> violations) {
        if (request.ocr().front() == null && request.ocr().back() == null) {
            violations.add("ocr: informe o texto de pelo menos um lado");
        }
    }

    private void checkPositions(List<ScanIngredientRequest> ingredients, List<String> violations) {
        if (ingredients == null) {
            return;
        }
        Set<Integer> seen = new HashSet<>();
        Set<Integer> repeated = new TreeSet<>();
        for (ScanIngredientRequest ingredient : ingredients) {
            if (!seen.add(ingredient.position())) {
                repeated.add(ingredient.position());
            }
        }
        repeated.forEach(position -> violations.add("ingredients: posicao " + position + " repetida"));
    }

    private void checkImages(ScanSessionRequest request, List<String> violations) {
        if (request.images() == null) {
            return;
        }
        checkImage(request.images().front(), request.scanId(), ScanCloudinaryService.FRONT, violations);
        checkImage(request.images().back(), request.scanId(), ScanCloudinaryService.BACK, violations);
    }

    private void checkImage(ScanImageRequest image, UUID scanId, String side, List<String> violations) {
        if (image == null) {
            return;
        }
        String expected = ScanCloudinaryService.publicIdFor(scanId, side);
        if (!image.publicId().equals(expected) && !image.publicId().endsWith("/" + expected)) {
            violations.add("images." + side + ".publicId: tem que terminar em " + expected);
        }
    }
}
