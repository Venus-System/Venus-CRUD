package com.venus.crud.mapper.mongo;

import java.util.Map;

public final class ScanUserNames {

    public static final ScanUserNames EMPTY = new ScanUserNames(Map.of());

    private final Map<Long, String> namesById;

    public ScanUserNames(Map<Long, String> namesById) {
        this.namesById = Map.copyOf(namesById);
    }

    public String nameOf(Long userId) {
        return userId == null ? null : namesById.get(userId);
    }
}
