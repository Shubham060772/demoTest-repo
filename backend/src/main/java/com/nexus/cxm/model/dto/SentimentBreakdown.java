package com.nexus.cxm.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SentimentBreakdown {
    private int positive;
    private int neutral;
    private int negative;
    private double positivePercent;
    private double negativePercent;
    private double neutralPercent;
}
