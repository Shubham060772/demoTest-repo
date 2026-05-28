package com.nexus.cxm.model.dto.input;

public record CustomerFilterInput(
        String search,
        String segment,
        String tag,
        String company
) {}
