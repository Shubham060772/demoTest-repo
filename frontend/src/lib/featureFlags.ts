/**
 * Feature-flag utilities for the frontend.
 *
 * Usage:
 *   import { isOn } from "@/lib/featureFlags";
 *
 *   isOn('CAMPAIGN_DASHBOARD_V2')       // boolean
 *   isOn('BULK_OPERATIONS', 'D')        // boolean (directory-scoped)
 */

/** Feature flags that are currently enabled. */
const ENABLED_FLAGS: ReadonlySet<string> = new Set([
  "USER_PROFILE_V2",
  "ADVANCED_ANALYTICS",
  "NEW_USER_FLOW",
  "CAMPAIGN_DASHBOARD_V2",
  "SMART_RECOMMENDATIONS",
  "BULK_OPERATIONS",
]);

/**
 * Returns true if the named feature flag is enabled.
 *
 * @param flag   e.g. 'ADVANCED_ANALYTICS'
 * @param scope  optional scope identifier (e.g. 'D' for directory-level)
 */
export function isOn(flag: string, scope?: string): boolean {
  // In demo mode scope does not affect the result.
  return ENABLED_FLAGS.has(flag);
}
