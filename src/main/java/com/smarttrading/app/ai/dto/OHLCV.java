package com.smarttrading.app.ai.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OHLCV {
    private double open;
    private double close;
    private double low;
    private double high;
    private double volume;
}
