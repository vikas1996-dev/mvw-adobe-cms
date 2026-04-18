package com.mvw.core.models;

import java.util.Comparator;

import com.mvw.core.models.dto.ResortDto;

public final class ResortComparator {

    private ResortComparator() {
        // utility class
    }

    public static Comparator<ResortDto> byLocaleAndSubLocalePriority() {
        return Comparator
                // 1️⃣ Locale priority
                .comparingInt((ResortDto r) -> parsePriority(
                        r.getLocale() != null ? r.getLocale().getPriority() : null))

                // 2️⃣ Sublocale priority
                .thenComparingInt(r -> parsePriority(
                        r.getSubLocale() != null ? r.getSubLocale().getPriority() : null))

                // 3️⃣ City alphabetical (A–Z)
                .thenComparing(
                        ResortDto::getCity,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                )

                // 4️⃣ Resort name alphabetical (final fallback)
                .thenComparing(
                        ResortDto::getName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                );
    }

    private static int parsePriority(String priority) {
        try {
            return priority != null ? Integer.parseInt(priority) : Integer.MAX_VALUE;
        } catch (NumberFormatException ex) {
            return Integer.MAX_VALUE;
        }
    }
}
