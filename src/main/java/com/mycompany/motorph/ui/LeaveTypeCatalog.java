package com.mycompany.motorph.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class LeaveTypeCatalog {

    private static final List<String> MASTER_LEAVE_TYPES = List.of(
            "Vacation Leave",
            "Sick Leave",
            "Emergency Leave",
            "Bereavement Leave"
    );

    private LeaveTypeCatalog() {
    }

    public static String[] masterLeaveTypes() {
        return MASTER_LEAVE_TYPES.toArray(String[]::new);
    }

    public static List<String> filterOptionsWithExtras(Collection<String> extraLeaveTypes) {
        List<String> ordered = new ArrayList<>(MASTER_LEAVE_TYPES);
        if (extraLeaveTypes == null) {
            return ordered;
        }

        extraLeaveTypes.stream()
                .filter(type -> type != null && !type.isBlank())
                .map(String::trim)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .forEach(type -> {
                    if (ordered.stream().noneMatch(existing -> existing.equalsIgnoreCase(type))) {
                        ordered.add(type);
                    }
                });

        return ordered;
    }
}
