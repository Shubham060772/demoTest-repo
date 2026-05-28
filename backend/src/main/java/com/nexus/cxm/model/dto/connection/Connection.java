package com.nexus.cxm.model.dto.connection;

import java.util.List;

public record Connection<T>(
        List<Edge<T>> edges,
        PageInfo pageInfo,
        int totalCount
) {
    public static <T> Connection<T> of(List<T> items, int first, String after) {
        int startIndex = 0;
        if (after != null) {
            try {
                startIndex = Integer.parseInt(after) + 1;
            } catch (NumberFormatException ignored) {}
        }

        int endIndex = Math.min(startIndex + (first > 0 ? first : 10), items.size());
        List<T> page = items.subList(Math.min(startIndex, items.size()), endIndex);

        List<Edge<T>> edges = new java.util.ArrayList<>();
        for (int i = 0; i < page.size(); i++) {
            edges.add(new Edge<>(page.get(i), String.valueOf(startIndex + i)));
        }

        String startCursor = edges.isEmpty() ? null : edges.get(0).cursor();
        String endCursor = edges.isEmpty() ? null : edges.get(edges.size() - 1).cursor();

        return new Connection<>(
                edges,
                new PageInfo(endIndex < items.size(), startIndex > 0, startCursor, endCursor),
                items.size()
        );
    }
}
