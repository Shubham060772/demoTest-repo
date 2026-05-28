# Cross-Repo Semantic Graph System — Complete Project Context for Repository Analysis

## Objective

You are analyzing a system that is evolving from a traditional dependency graph generator into a **Cross-Repo Semantic Knowledge Graph**.

The purpose of this analysis is:

1. Understand both frontend and backend repositories
2. Determine how frontend GraphQL operations connect to backend resolvers/services
3. Identify how to build a semantic graph that links:

   * Frontend components
   * GraphQL queries
   * Schema fields
   * Resolvers
   * Services
   * Entities
   * Feature flows

The final goal is NOT simple dependency visualization.

The target system is much closer to:

* Static analysis engines
* Semantic code intelligence systems
* Compiler-style analyzers
* Architecture knowledge graphs

---

# Important Conceptual Foundation

## Traditional Dependency Graph (Current)

Current graph mainly captures:

```text
File A -> imports -> File B
```

This only represents:

* Structural dependencies
* Syntactic relationships

This is insufficient.

---

# Key Insight About GraphQL

GraphQL systems usually expose:

```text
/graphql
```

as a single endpoint.

Therefore:

* URL matching is mostly useless
* Route-based linking does not work

Instead connectivity must be inferred semantically through:

```text
Frontend Query
    ↓
Schema Field
    ↓
Resolver
    ↓
Service
    ↓
Entity / DB
```

---

# Desired Semantic Graph

Example:

```text
UserPage.tsx
    ↓ EXECUTES
GetUserQuery
    ↓ REQUESTS
Query.user
    ↓ RESOLVED_BY
UserResolver.user
    ↓ CALLS
UserService.getUser
```

Nested field example:

```text
Query.user.posts
    ↓ RESOLVED_BY
PostResolver.posts
```

---

# MOST IMPORTANT ARCHITECTURAL REALIZATION

This project should be treated like a:

# Compiler / Static Analysis Pipeline

NOT as a visualization tool.

Pipeline:

```text
Code
  ↓
AST
  ↓
Semantic Extraction
  ↓
Intermediate Representation (IR)
  ↓
Semantic Matching
  ↓
Graph Construction
```

---

# Two AST Layers (Critical)

## Layer 1 — Babel AST

Purpose:

* Parse TS/JS/React source
* Detect gql/graphql/useQuery
* Extract GraphQL query strings

This AST understands JavaScript syntax.

Example:

```ts
const GET_USER = gql`
  query GetUser {
    user {
      id
    }
  }
`
```

Detected as:

```text
TaggedTemplateExpression
```

Babel AST DOES NOT understand GraphQL semantics.

---

## Layer 2 — GraphQL AST

Purpose:

* Parse GraphQL query itself
* Understand:

  * fields
  * nesting
  * fragments
  * aliases
  * directives

Using:

```ts
import { parse } from "graphql"
```

Example:

```graphql
query GetUser {
  user {
    posts {
      title
    }
  }
}
```

Conceptual AST:

```text
Query(GetUser)
  └── user
        └── posts
              └── title
```

This hierarchy is essential.

---

# Why AST Is Mandatory

Bad representation:

```json
{
  "query": "query {...}"
}
```

Good representation:

```json
{
  "name": "user",
  "children": [
    {
      "name": "posts",
      "children": [
        {
          "name": "title"
        }
      ]
    }
  ]
}
```

AST is required because it preserves:

* hierarchy
* traversal
* semantic structure
* nested execution flow

---

# Target Semantic Node Types

```ts
type NodeType =
  | "FILE"
  | "GRAPHQL_OPERATION"
  | "GRAPHQL_FIELD"
  | "GRAPHQL_TYPE"
  | "RESOLVER"
  | "SERVICE"
```

---

# Target Semantic Edge Types

```ts
type EdgeType =
  | "IMPORTS"
  | "EXECUTES"
  | "REQUESTS"
  | "RESOLVED_BY"
  | "CALLS"
  | "RETURNS"
```

---

# IMPORTANT DESIGN PRINCIPLE

DO NOT create graph edges directly inside extractors.

Correct architecture:

```text
Extractor
   ↓
IR
   ↓
Matcher
   ↓
Graph Builder
```

This separation is mandatory for extensibility.

---

# Current Phase Goal

## Phase 1 Objective

Only focus on:

# Frontend GraphQL Semantic Extraction

No:

* backend matching
* embeddings
* AI
* graph database
* runtime execution

Goal:
Extract semantic IR from frontend GraphQL operations.

---

# Current Extraction Flow

```text
TS/JS File
   ↓
Babel Parser
   ↓
JS AST Traversal
   ↓
Locate gql/graphql/useQuery
   ↓
Extract Query String
   ↓
GraphQL Parser
   ↓
GraphQL AST Traversal
   ↓
IR Generation
```

---

# Current IR Structure

## GraphQLField

```ts
export interface GraphQLField {
  name: string
  alias?: string
  children: GraphQLField[]
}
```

## GraphQLOperation

```ts
export interface GraphQLOperation {
  operationName: string
  operationType: "query" | "mutation" | "subscription"
  sourceFile: string
  rootFields: GraphQLField[]
}
```

---

# Expected Frontend Extraction

The analyzer should identify:

## Example

Frontend file:

```ts
const GET_USER = gql`
query GetUser {
  user {
    id
    posts {
      title
    }
  }
}
`
```

Expected IR:

```json
{
  "operationName": "GetUser",
  "operationType": "query",
  "sourceFile": "UserPage.tsx",
  "rootFields": [
    {
      "name": "user",
      "children": [
        {
          "name": "id",
          "children": []
        },
        {
          "name": "posts",
          "children": [
            {
              "name": "title",
              "children": []
            }
          ]
        }
      ]
    }
  ]
}
```

---

# What The Repository Analysis Should Discover

You should inspect the repositories and determine:

## FRONTEND SIDE

### 1. GraphQL Client Pattern

Identify:

* Apollo Client?
* Relay?
* urql?
* graphql-request?
* custom wrappers?

Find:

* gql tagged templates
* useQuery/useMutation hooks
* generated SDKs
* fragments
* query organization

---

### 2. Query Location Strategy

Determine:

* inline queries?
* separate .graphql files?
* generated code?
* codegen usage?

---

### 3. Operation Ownership

Determine:

* which React components execute which operations
* screen-level execution mapping

Goal:

```text
Component.tsx
   ↓ EXECUTES
Operation
```

---

### 4. Entity Propagation

Track:

* response types
* props flow
* cache usage
* normalized entities

---

# BACKEND SIDE

You should determine:

## 1. GraphQL Server Framework

Examples:

* Apollo Server
* NestJS GraphQL
* graphql-java
* Spring GraphQL
* Mercurius
* Yoga

---

## 2. Resolver Discovery Strategy

Determine:

* how resolvers are declared
* schema-first vs code-first
* annotations/decorators used

Examples:

```ts
@Query()
user() {}
```

or

```java
@QueryMapping
```

---

## 3. Schema Ownership

Map:

```text
Query.user
```

to:

```text
UserResolver.user
```

---

## 4. Service Layer Connectivity

Track:

* resolver → service
* service → repository
* service → DB/entity

---

# MAIN PROBLEM TO SOLVE

The critical challenge is:

# How to connect frontend semantic graph and backend semantic graph

The matching likely occurs at:

```text
Frontend Field Path
    ↔
Schema Field
    ↔
Resolver
```

---

# Recommended Matching Strategy

## Stage 1

Frontend extraction produces:

```text
Operation → Field Paths
```

Example:

```text
Query.user.posts.title
```

---

## Stage 2

Backend extraction produces:

```text
Query.user
User.posts
Post.title
```

along with resolver ownership.

---

## Stage 3

Semantic matcher links:

```text
Frontend Field Path
      ↔
Backend Resolver Path
```

---

# Recommended Graph Architecture

## Frontend Layer

```text
FILE
  ↓ EXECUTES
GRAPHQL_OPERATION
  ↓ REQUESTS
GRAPHQL_FIELD
```

---

## Backend Layer

```text
GRAPHQL_FIELD
  ↓ RESOLVED_BY
RESOLVER
  ↓ CALLS
SERVICE
```

---

# Suggested Matching Rules

Potential rules:

## Rule 1 — Root Query Match

```text
query.user
```

matches backend:

```text
Query.user
```

---

## Rule 2 — Nested Type Expansion

If:

```graphql
user {
  posts
}
```

and schema says:

```graphql
type User {
  posts: [Post]
}
```

then:

```text
User.posts
```

should connect to:

* PostResolver.posts
  or field resolver implementation

---

## Rule 3 — Resolver Ownership

Match resolver decorators/signatures to schema fields.

---

# Important Scope Discipline

Initially IGNORE:

* fragments
* directives
* aliases
* subscriptions
* dynamic query construction
* federation
* runtime-generated queries

Support ONLY:

```ts
gql`...`
```

with static queries.

---

# Important Repository Questions To Answer

After reading the repositories, answer:

## Frontend Questions

1. How are GraphQL queries organized?
2. How are queries executed?
3. Which components own which operations?
4. Is there generated GraphQL code?
5. How are entities propagated through UI?
6. What extraction strategy best fits this repo?

---

## Backend Questions

1. What GraphQL framework is used?
2. Is schema-first or code-first used?
3. How are resolvers defined?
4. How can schema fields be mapped to resolvers?
5. How are services invoked?
6. What semantic node types exist naturally?

---

## Cross-Repo Questions

1. What is the strongest semantic join point?
2. How should field paths be normalized?
3. Should matching happen at:

   * operation level?
   * field level?
   * resolver level?
4. How should nested field traversal work?
5. How should the graph builder merge both repositories?
6. What IR changes are needed for full-stack connectivity?

---

# Desired Final Output From Repository Analysis

The repository analysis should ultimately produce:

## 1. Recommended Semantic Extraction Architecture

Specific to these repositories.

---

## 2. Frontend Semantic Extraction Strategy

Including:

* AST traversal approach
* query discovery
* operation ownership
* field extraction

---

## 3. Backend Semantic Extraction Strategy

Including:

* resolver extraction
* schema extraction
* service mapping

---

## 4. Cross-Repo Matching Strategy

How frontend and backend graphs should connect.

---

## 5. Recommended IR Design

Additional fields/nodes/edges needed.

---

## 6. Recommended Graph Construction Pipeline

End-to-end semantic graph pipeline.

---

# Long-Term Vision

Eventually the graph should support:

## Impact Analysis

```text
If User.posts changes,
which frontend screens break?
```

---

## Flow Tracing

```text
frontend
  → operation
  → resolver
  → service
  → DB
```

---

## Dead Resolver Detection

```text
Which GraphQL fields are never queried?
```

---

## AI-Assisted Architecture Understanding

Examples:

* Explain checkout flow
* Which services power dashboard?
* Which frontend screens depend on payment service?

---

# Most Important Conceptual Outcome

This project is fundamentally becoming:

# Cross-Repo Semantic System Graph

NOT merely:

# File Dependency Graph

This conceptual distinction is the foundation of the system.

---

# Why Field-Wise Connections Are Mandatory

It is tempting to map the frontend Operation (e.g., query GetUser) directly to the backend Root Resolver (e.g., Query.user) and skip nested fields. However, stopping at the root query treats GraphQL exactly like a REST API and destroys the value of a semantic graph.

Here are the fatal flaws with skipping field-wise connections:

## 1. Missing Hidden Dependencies (Field Resolvers)
In GraphQL, the root resolver doesn't always fetch all the data. 
If a frontend component queries customer(id: 1) { name, messages { body } }, the messages field might be resolved by a separate @SchemaMapping (e.g., CustomerFieldResolver.messages) which calls MessageService. By not tracking the messages field, the graph completely misses the dependency on MessageService.

## 2. Impossible "Dead Code" Detection
One of the most powerful use cases for a semantic graph is safely cleaning up old code. 
- **With Field-Wise Connections:** The graph shows exactly 0 frontend components asking for a field like Customer.legacyFaxNumber. It can be safely deleted.
- **With Root-Only Connections:** The graph only shows that the frontend calls the customer root query, but not *which* fields it needs. No field can ever be safely deleted.

## 3. Overly Broad Impact Analysis
If a backend engineer changes how Campaign.budget is calculated:
- **With Field-Wise Connections:** The graph says *"Only the CampaignDetail and Dashboard screens use the udget field. Test those two screens."*
- **With Root-Only Connections:** The graph says *"Any screen that fetches a Campaign might be broken."* This forces testing of every campaign-related screen, rendering the graph unhelpful.
