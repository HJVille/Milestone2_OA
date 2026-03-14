package com.mycompany.motorph.service;

import com.mycompany.motorph.model.User;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AccessControlService {

    private static final Map<String, List<String>> ROLE_FEATURES = new LinkedHashMap<>();

    static {
        ROLE_FEATURES.put("HR", List.of(
                "View employee records",
                "Add employee",
                "Edit employee",
                "Delete employee",
                "View government IDs",
                "Review leave requests",
                "View attendance"
        ));
        ROLE_FEATURES.put("FINANCE", List.of(
                "Process payroll",
                "View payroll records",
                "Generate payslips",
                "Review payroll history"
        ));
        ROLE_FEATURES.put("PAYROLL", ROLE_FEATURES.get("FINANCE"));
        ROLE_FEATURES.put("IT", List.of(
                "Manage user accounts",
                "Review CSV data paths",
                "Review audit logs",
                "Reset account passwords"
        ));
        ROLE_FEATURES.put("ADMIN", List.of(
                "View employee records",
                "Add employee",
                "Edit employee",
                "Delete employee",
                "View government IDs",
                "View attendance",
                "Review leave requests",
                "Process payroll",
                "View payroll records",
                "Generate payslips",
                "Manage user accounts",
                "Review CSV data paths",
                "Review audit logs",
                "Reset account passwords"
        ));
        ROLE_FEATURES.put("EMPLOYEE", List.of(
                "View profile",
                "View payslip",
                "View government IDs",
                "Submit leave request",
                "View notifications",
                "Change password"
        ));
    }

    public boolean canAccess(User user, String feature) {

        if (user == null) {
            return false;
        }

        return canAccess(user.getRole(), feature);
    }

    public boolean hasRole(User user, String... allowedRoles) {
        if (user == null || allowedRoles == null || allowedRoles.length == 0) {
            return false;
        }
        return hasRole(user.getRole(), allowedRoles);
    }

    public boolean hasRole(String role, String... allowedRoles) {
        if (role == null || allowedRoles == null || allowedRoles.length == 0) {
            return false;
        }

        String normalizedRole = normalizeRole(role);
        for (String allowedRole : allowedRoles) {
            if (normalizeRole(allowedRole).equals(normalizedRole)) {
                return true;
            }
        }
        return false;
    }

    public boolean canAccess(String role, String feature) {

        if (role == null || feature == null) {
            return false;
        }

        String normalizedFeature = feature.trim().toLowerCase(Locale.ROOT);
        for (String allowedFeature : getAllowedFeatures(role)) {
            if (allowedFeature.toLowerCase(Locale.ROOT).equals(normalizedFeature)) {
                return true;
            }
        }

        return false;
    }

    public List<String> getAllowedFeatures(User user) {

        if (user == null) {
            return List.of();
        }

        return getAllowedFeatures(user.getRole());
    }

    public List<String> getAllowedFeatures(String role) {

        if (role == null) {
            return List.of();
        }

        List<String> features = ROLE_FEATURES.get(normalizeRole(role));
        if (features == null) {
            return List.of();
        }

        return new ArrayList<>(features);
    }

    private String normalizeRole(String role) {
        String normalized = role == null ? "" : role.trim().toUpperCase(Locale.ROOT);
        if ("PAYROLL".equals(normalized)) {
            return "FINANCE";
        }
        return normalized;
    }
}
