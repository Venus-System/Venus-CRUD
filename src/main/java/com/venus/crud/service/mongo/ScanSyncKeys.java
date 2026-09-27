package com.venus.crud.service.mongo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ScanSyncKeys {

    private static final Pattern MARKS = Pattern.compile("\\p{M}+");
    private static final Pattern NOT_LETTER_OR_DIGIT = Pattern.compile("[^a-z0-9]+");
    private static final Pattern EDGE_HYPHENS = Pattern.compile("^-+|-+$");
    private static final String SEPARATOR = "|";

    private ScanSyncKeys() {
    }

    public static String slug(String brandName, String productName) {
        String decomposed = Normalizer.normalize(brandName + " " + productName, Normalizer.Form.NFD);
        String withoutAccents = MARKS.matcher(decomposed).replaceAll("").toLowerCase(Locale.ROOT);
        String hyphenated = NOT_LETTER_OR_DIGIT.matcher(withoutAccents).replaceAll("-");
        return EDGE_HYPHENS.matcher(hyphenated).replaceAll("");
    }

    public static String formulaSignature(List<Long> ingredientIdsByPosition) {
        String joined = ingredientIdsByPosition.stream().map(String::valueOf).collect(Collectors.joining(SEPARATOR));
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(joined.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 indisponivel", ex);
        }
    }
}
