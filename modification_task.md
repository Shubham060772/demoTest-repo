# Objective

Update this repository to include realistic feature-flag and permission dependencies throughout the codebase so that dependency-analysis tooling can discover and traverse them.

This is NOT a security implementation task.

The purpose is to create representative usages of:

Frontend:

* `can('p:permission_name')`
* `AclStore.can('p:permission_name')`
* `isOn('FEATURE_FLAG')`
* `isOn('FEATURE_FLAG', 'D')`

Backend:

* Permission annotations/checks
* Feature-flag checks

The inserted usages should appear naturally in the code and should influence UI visibility, actions, GraphQL queries, mutations, pages, and backend resolvers.

---

# Existing Patterns

Before making changes:

1. Search the repository for:

   * `can(`
   * `AclStore.can(`
   * `isOn(`
   * permission-related annotations
   * feature-flag services

2. Reuse the exact existing APIs and coding style already used in this repository.

3. Do NOT invent a new permission framework.

---

# Frontend Changes

Add permission and feature-flag checks in multiple layers.

## React Pages

Examples:

```tsx
if (!can('p:user_view')) {
  return <AccessDenied />;
}
```

```tsx
const showAnalytics = isOn('USER_ANALYTICS_V2');
```

---

## Components

Gate rendering:

```tsx
{can('p:user_edit') && (
  <EditButton />
)}
```

```tsx
{isOn('USER_PROFILE_V2') && (
  <NewProfileCard />
)}
```

---

## Action Menus

Add permissions to action visibility:

```tsx
{
  label: 'Delete',
  hidden: !AclStore.can('p:user_delete')
}
```

---

## Hooks

Examples:

```tsx
const canManageUsers = can('p:user_manage');
```

```tsx
const useNewFlow = isOn('NEW_USER_FLOW');
```

---

## GraphQL Consumers

Place checks near GraphQL usage:

```tsx
const shouldFetchAdvancedData =
  can('p:advanced_analytics') &&
  isOn('ADVANCED_ANALYTICS');
```

```tsx
const { data } = useQuery(
  shouldFetchAdvancedData
    ? AdvancedAnalyticsQuery
    : BasicAnalyticsQuery
);
```

---

# Backend Changes

Search for GraphQL resolvers, controllers, services, and business logic.

Add permission and feature-flag checks close to GraphQL entry points.

## GraphQL Query Resolvers

Examples:

```java
if (!permissionService.can("p:user_view")) {
    throw new AccessDeniedException();
}
```

---

## GraphQL Mutation Resolvers

Examples:

```java
if (!permissionService.can("p:user_edit")) {
    throw new AccessDeniedException();
}
```

---

## Service Layer

Examples:

```java
boolean useNewFlow =
    featureFlagService.isOn("USER_PROFILE_V2");
```

---

## Resolver-Level Flags

Examples:

```java
if (featureFlagService.isOn("ADVANCED_ANALYTICS")) {
    return analyticsService.getAdvancedData();
}
```

---

# GraphQL-Centric Requirement

Prefer adding checks around:

* GraphQL queries
* GraphQL mutations
* GraphQL resolver methods
* GraphQL service methods
* Components that consume GraphQL data
* Hooks wrapping GraphQL queries
* Action handlers triggered by GraphQL mutations

The goal is to create dependency paths such as:

Frontend Component
→ Permission

Frontend Component
→ Feature Flag

Frontend Component
→ GraphQL Query

GraphQL Query
→ Resolver

Resolver
→ Permission

Resolver
→ Feature Flag

Resolver
→ Service

Service
→ Feature Flag

---

# Permission Names

Use realistic names:

```text
p:user_view
p:user_edit
p:user_delete
p:user_manage
p:campaign_view
p:campaign_edit
p:campaign_publish
p:analytics_view
p:advanced_analytics
p:admin_access
```

---

# Feature Flag Names

Use realistic names:

```text
USER_PROFILE_V2
ADVANCED_ANALYTICS
NEW_USER_FLOW
CAMPAIGN_DASHBOARD_V2
SMART_RECOMMENDATIONS
BULK_OPERATIONS
```

---

# Coverage Target

Create at least:

* 10+ frontend permission usages
* 10+ frontend feature-flag usages
* 5+ GraphQL query/mutation gated usages
* 10+ backend permission usages
* 10+ backend feature-flag usages

Spread them across multiple files rather than concentrating them in one place.

---

# Important

Do not break builds.

Reuse existing repository APIs.

Prefer modifying existing files instead of creating artificial demo files.

After changes, provide a report listing:

* File changed
* Permission names added
* Feature flags added
* GraphQL operations affected
* Backend resolvers affected
