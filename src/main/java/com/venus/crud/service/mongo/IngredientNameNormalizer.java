package com.venus.crud.service.mongo;

import java.util.Locale;
import java.util.regex.Pattern;

public final class IngredientNameNormalizer {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern TRAILING_MARKS = Pattern.compile("[\\s.*,;]+$");

    private IngredientNameNormalizer() {
    }

    public static String normalize(String rawName) {
        if (rawName == null) {
            return null;
        }
        String collapsed = WHITESPACE.matcher(rawName.strip()).replaceAll(" ");
        return TRAILING_MARKS.matcher(collapsed).replaceAll("").toUpperCase(Locale.ROOT);
    }
}
