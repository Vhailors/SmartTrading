package com.smarttrading.app.ta.dto;

import com.smarttrading.app.ta.indicators.candlestickpatterns.CandleStickPattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaResponse {
    CandleStickPattern pattern;
    Trend direction;
}
