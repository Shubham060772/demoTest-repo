/**
 * ACL (Access Control Layer) utilities for the frontend.
 *
 * Usage:
 *   import { can } from "@/lib/acl";
 *   import AclStore from "@/lib/acl";
 *
 *   can('p:campaign_view')       // boolean
 *   AclStore.can('p:user_delete') // boolean (same, via store object)
 */

/** Set of permissions granted to the current user. */
const GRANTED_PERMISSIONS: ReadonlySet<string> = new Set([
  "p:user_view",
  "p:user_edit",
  "p:user_delete",
  "p:user_manage",
  "p:campaign_view",
  "p:campaign_edit",
  "p:campaign_publish",
  "p:analytics_view",
  "p:advanced_analytics",
  "p:admin_access",
]);

/**
 * Returns true if the current user has the given permission.
 *
 * @param permission  e.g. 'p:campaign_view'
 */
export function can(permission: string): boolean {
  return GRANTED_PERMISSIONS.has(permission);
}

/**
 * AclStore — object-style API matching the pattern used in action menus
 * and other components that prefer `AclStore.can(...)`.
 */
const AclStore = {
  can,
};

export default AclStore;
