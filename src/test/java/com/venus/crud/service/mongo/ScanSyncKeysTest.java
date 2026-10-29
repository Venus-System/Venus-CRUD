package com.venus.crud.service.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ScanSyncKeysTest {

    @Test
    void slugRemovesAccentsAndSymbols() {
        assertThat(ScanSyncKeys.slug("Natura", "Sérum Vitamina C 30ml!")).isEqualTo("natura-serum-vitamina-c-30ml");
    }

    @Test
    void slugHasNoHyphenAtTheEdgesNorRepeated() {
        assertThat(ScanSyncKeys.slug("  L'Oréal ", "--Elseve--")).isEqualTo("l-oreal-elseve");
    }

    @Test
    void sameIngredientsGiveTheSameSignature() {
        String signature = ScanSyncKeys.formulaSignature(List.of(100L, 201L, 355L));

        assertThat(signature).isEqualTo(ScanSyncKeys.formulaSignature(List.of(100L, 201L, 355L)));
        assertThat(signature).matches("[0-9a-f]{64}");
    }

    @Test
    void anotherOrderGivesAnotherSignature() {
        assertThat(ScanSyncKeys.formulaSignature(List.of(100L, 201L)))
                .isNotEqualTo(ScanSyncKeys.formulaSignature(List.of(201L, 100L)));
    }
}
