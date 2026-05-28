# Semantic Graph Examples: Field-Wise vs Root-Only

This document provides a side-by-side comparison of two approaches to building the Semantic Graph, based on a real query in the application.

## The Scenario

Assume `src/app/campaigns/[id]/page.tsx` executes this query:

```graphql
query GetCampaignDetail($id: ID!) {
  campaign(id: $id) {
    id
    name
    budget
    channels {
      id
      name
    }
  }
}
```

---

## Example 1: Field-Wise Matching (Recommended)

This is the correct approach. It traverses the nested fields (`campaign` -> `channels`), correctly discovering that `channels` is resolved by a separate field resolver which relies on `ChannelService`. 

*(Note: Line breaks `<br/>` are used in the nodes below to prevent text clipping in the viewer.)*

```mermaid
graph TD
    %% Frontend Nodes
    File["File:<br/>campaigns/[id]/page.tsx"]
    Op["Operation:<br/>GetCampaignDetail"]
    F_Camp["Field:<br/>Query.campaign"]
    F_Camp_Channels["Field:<br/>Campaign.channels"]
    
    %% Backend Nodes
    Res_Camp["Resolver:<br/>CampaignQueryResolver"]
    Svc_Camp["Service:<br/>CampaignService"]
    
    Res_Chan["Resolver:<br/>CampaignFieldResolver"]
    Svc_Chan["Service:<br/>CampaignService<br/>(getChannels)"]

    %% Edges
    File -->|EXECUTES| Op
    Op -->|REQUESTS| F_Camp
    Op -->|REQUESTS| F_Camp_Channels
    
    F_Camp -->|RESOLVED_BY| Res_Camp
    Res_Camp -->|CALLS| Svc_Camp
    
    F_Camp_Channels -->|RESOLVED_BY| Res_Chan
    Res_Chan -->|CALLS| Svc_Chan
    
    %% Styling
    classDef frontend fill:#1e3a8a,stroke:#3b82f6,color:white;
    classDef backend fill:#064e3b,stroke:#10b981,color:white;
    classDef match fill:#78350f,stroke:#f59e0b,color:white;
    
    class File,Op frontend;
    class F_Camp,F_Camp_Channels match;
    class Res_Camp,Svc_Camp,Res_Chan,Svc_Chan backend;
```

---

## Example 2: Root-Only Matching (Flawed)

This demonstrates what happens if you try to link the Operation directly to the Root Query name and stop there. 

Because we stop at `Query.campaign`, we **completely lose** the fact that the frontend is asking for `channels`, and therefore we completely lose the dependency on the `CampaignFieldResolver`.

```mermaid
graph TD
    %% Frontend Nodes
    File["File:<br/>campaigns/[id]/page.tsx"]
    Op["Operation:<br/>GetCampaignDetail"]
    
    %% Backend Nodes
    Res_Camp["Resolver:<br/>CampaignQueryResolver"]
    Svc_Camp["Service:<br/>CampaignService"]

    %% Edges
    File -->|EXECUTES| Op
    Op -->|RESOLVED_BY| Res_Camp
    Res_Camp -->|CALLS| Svc_Camp
    
    %% Styling
    classDef frontend fill:#1e3a8a,stroke:#3b82f6,color:white;
    classDef backend fill:#064e3b,stroke:#10b981,color:white;
    
    class File,Op frontend;
    class Res_Camp,Svc_Camp backend;
```

### Why Example 2 is flawed:
In Example 2, if a backend engineer modifies the logic for fetching channels in `CampaignFieldResolver`, the graph will **not** show that `campaigns/[id]/page.tsx` is affected, because the edge to the channels resolver is missing entirely. This makes the graph unreliable for impact analysis.
