package com.venus.crud.dto.mongo.response.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.AnalysisResultFullResponse;
import com.venus.crud.dto.jpa.response.fullstage.ProductFullResponse;
import com.venus.crud.dto.jpa.response.product.ProductVersionResponse;
import com.venus.crud.dto.mongo.response.ScanSessionResponse;

public record ScanFullResponse(
        ScanSessionResponse scan,
        AnalysisResultFullResponse analysis,
        ProductFullResponse product,
        ProductVersionResponse analyzedVersion,
        boolean productChangedSinceScan
) {
}
