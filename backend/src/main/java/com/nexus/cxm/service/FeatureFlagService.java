package com.nexus.cxm.service;

import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Simple feature-flag checking service used by resolvers and services.
 *
 * Usage:
 *   boolean v2 = featureFlagService.isOn("CAMPAIGN_DASHBOARD_V2");
 *   boolean dir = featureFlagService.isOn("BULK_OPERATIONS", "D");
 *
 * NOTE: In this demonstration codebase all flags are enabled so that the
 * application remains fully functional.  Replace the enabled set or the
 * lookup logic with a real feature-flag backend (LaunchDarkly, Unleash, etc.)
 * as needed.
 */
@Service
public class FeatureFlagService {

    /**
     * Feature flags that are currently enabled.
     * Represents all flags being on (demo mode).
     */
    private static final Set<String> ENABLED_FLAGS = Set.of(
            "USER_PROFILE_V2",
            "ADVANCED_ANALYTICS",
            "NEW_USER_FLOW",
            "CAMPAIGN_DASHBOARD_V2",
            "SMART_RECOMMENDATIONS",
            "BULK_OPERATIONS"
    );

    /**
     * Returns {@code true} if the named flag is currently enabled.
     *
     * @param flag the flag name, e.g. {@code "ADVANCED_ANALYTICS"}
     */
    public boolean isOn(String flag) {
        return ENABLED_FLAGS.contains(flag);
    }

    /**
     * Returns {@code true} if the named flag is enabled for the given scope.
     * Scope {@code "D"} means the default (global) directory scope.
     *
     * @param flag  the flag name
     * @param scope the scope identifier (e.g. {@code "D"} for directory-level)
     */
    public boolean isOn(String flag, String scope) {
        // In demo mode scope does not affect the result; honour only the flag name.
        return isOn(flag);
    }
}
