package com.nexus.cxm.service;

import com.nexus.cxm.exception.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Simple permission-checking service used by resolvers and services.
 *
 * Usage:
 *   permissionService.require("p:campaign_view");   // throws if denied
 *   boolean ok = permissionService.can("p:user_edit"); // returns boolean
 *
 * NOTE: In this demonstration codebase all permissions are granted so that
 * the application remains fully functional.  Replace the allowed set or the
 * load logic with real role/principal lookups as needed.
 */
@Service
public class PermissionService {

    /**
     * Permissions that are currently active for the authenticated principal.
     * Represents all permissions being granted (demo mode).
     */
    private static final Set<String> GRANTED_PERMISSIONS = Set.of(
            "p:user_view",
            "p:user_edit",
            "p:user_delete",
            "p:user_manage",
            "p:campaign_view",
            "p:campaign_edit",
            "p:campaign_publish",
            "p:analytics_view",
            "p:advanced_analytics",
            "p:admin_access"
    );

    /**
     * Returns {@code true} if the current principal holds the given permission.
     *
     * @param permission the permission key, e.g. {@code "p:campaign_view"}
     */
    public boolean can(String permission) {
        return GRANTED_PERMISSIONS.contains(permission);
    }

    /**
     * Asserts that the current principal holds the given permission.
     * Throws {@link AccessDeniedException} otherwise.
     *
     * @param permission the permission key, e.g. {@code "p:campaign_edit"}
     */
    public void require(String permission) {
        if (!can(permission)) {
            throw new AccessDeniedException(
                    "Access denied: missing permission '" + permission + "'");
        }
    }
}
