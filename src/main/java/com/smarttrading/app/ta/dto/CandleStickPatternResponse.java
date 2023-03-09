package com.smarttrading.app.ta.dto;

import com.smarttrading.app.ta.indicators.candlestickpatterns.CandleStickPattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CandleStickPatternResponse {
    CandleStickPattern pattern;
    Trend direction;
}
